package br.com.waltercoan.apppalestra.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.azure.core.credential.TokenCredential;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.identity.DefaultAzureCredentialBuilder;

import br.com.waltercoan.apppalestra.config.AppConfigProperties;
import br.com.waltercoan.apppalestra.model.Participante;
import java.util.Optional;

@Component
public class ParticipanteDAO {
    private TableServiceClient tableServiceClient;
    private TableClient tableClient;
    @Value("${apppalestra.clientid:#{null}}")
    private Optional<String> mngIdentityClientId;
    @Autowired
    private AppConfigProperties properties;

    @Recover()
    public void tableConnect() {
        var credential  = new DefaultAzureCredentialBuilder().build();
        if(mngIdentityClientId.isPresent())
            credential = new DefaultAzureCredentialBuilder()
                    .managedIdentityClientId(mngIdentityClientId.get()).build();

        tableServiceClient = new TableServiceClientBuilder()
                .endpoint(properties.getStorageTable())
                .credential(credential)
                .buildClient();
        tableClient = tableServiceClient.getTableClient(properties.getTableName());
    }
    @Retryable
    public Participante save(Participante participante){
        if(tableClient == null) tableConnect();
        var uuid = UUID.randomUUID().toString();
        participante.setPartitionKey(uuid);
        participante.setRowKey(uuid);
        TableEntity entity = new TableEntity(participante.getPartitionKey(), participante.getRowKey())
            .addProperty("nome", participante.getNome())
            .addProperty("cidade", participante.getCidade())
            .addProperty("obs", participante.getObservacao());

        
        tableClient.createEntity(entity);
        return participante;
    }
    @Retryable 
    public List<Participante> getAll(){
        if(tableClient == null) tableConnect();
        List<Participante> listaParticipantes = new ArrayList<>();
        ListEntitiesOptions options = new ListEntitiesOptions();
        for (TableEntity entity : tableClient.listEntities(options, null, null)) {
            Map<String, Object> properties = entity.getProperties();
            var participante = new Participante();
            participante.setNome(properties.get("nome").toString());
            participante.setCidade(properties.get("cidade").toString());
            participante.setObservacao(properties.get("obs").toString());
            listaParticipantes.add(participante);
        }     
        return listaParticipantes;
    }
}
