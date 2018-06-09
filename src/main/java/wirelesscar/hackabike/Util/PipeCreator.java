package wirelesscar.hackabike.Util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.iotanalytics.AWSIoTAnalytics;
import com.amazonaws.services.iotanalytics.AWSIoTAnalyticsClientBuilder;
import com.amazonaws.services.iotanalytics.model.ChannelActivity;
import com.amazonaws.services.iotanalytics.model.CreateDatasetRequest;
import com.amazonaws.services.iotanalytics.model.CreatePipelineRequest;
import com.amazonaws.services.iotanalytics.model.DatasetAction;
import com.amazonaws.services.iotanalytics.model.DatastoreActivity;
import com.amazonaws.services.iotanalytics.model.LambdaActivity;
import com.amazonaws.services.iotanalytics.model.PipelineActivity;
import com.amazonaws.services.iotanalytics.model.SqlQueryDatasetAction;

public class PipeCreator {
  AWSIoTAnalytics client = AWSIoTAnalyticsClientBuilder
      .standard()
      .withRegion("us-east-1")
      .build();

  public void createPipe() {
    CreatePipelineRequest pipelineRequest = new CreatePipelineRequest();
    PipelineActivity activity1 = new PipelineActivity();
    PipelineActivity activity2 = new PipelineActivity();
    PipelineActivity activity3 = new PipelineActivity();

    ChannelActivity channelActivity = new ChannelActivity();
    channelActivity.setChannelName("jenstest");
    channelActivity.setName("jenstest28");
    channelActivity.setNext("lambdaactivity");

    LambdaActivity lambda = new LambdaActivity();
    lambda.setBatchSize(1);
    lambda.setLambdaName("ProcessPositionInput");
    lambda.setName("lambdaactivity");
    lambda.setNext("storeData");

    DatastoreActivity datastore = new DatastoreActivity();
    datastore.setDatastoreName("dagtvofinal");
    datastore.setName("storeData");

    activity1.setChannel(channelActivity);
    activity2.setLambda(lambda);
    activity3.setDatastore(datastore);

    pipelineRequest.setPipelineName("generatedPipeline");
    pipelineRequest.setPipelineActivities(Stream.of(activity1, activity2, activity3).collect(Collectors.toList()));
    client.createPipeline(pipelineRequest);
  }

  public void createDataSet() {
    CreateDatasetRequest createDatasetRequest = new CreateDatasetRequest();
    createDatasetRequest.setDatasetName("generatedDataset");
    DatasetAction action = new DatasetAction();
    action.setActionName("query");
    SqlQueryDatasetAction sql = new SqlQueryDatasetAction();
    sql.setSqlQuery("select * from dagtvofinal");
    action.setQueryAction(sql);
    createDatasetRequest.setActions(Stream.of(action).collect(Collectors.toList()));

    client.createDataset(createDatasetRequest);
  }
}
