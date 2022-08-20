package com.mycompany.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.hsr.servicecutter.api.EntityRelationDiagramImporterJSON;
import ch.hsr.servicecutter.api.ServiceCutter;
import ch.hsr.servicecutter.api.ServiceCutterContext;
import ch.hsr.servicecutter.api.ServiceCutterContextBuilder;
import ch.hsr.servicecutter.api.SolverConfigurationFactory;
import ch.hsr.servicecutter.api.UserRepresentationContainerImporterJSON;
import ch.hsr.servicecutter.api.model.EntityRelationDiagram;
import ch.hsr.servicecutter.api.model.SolverResult;
import ch.hsr.servicecutter.api.model.UserRepresentationContainer;
import ch.hsr.servicecutter.solver.SolverConfiguration;

public class App
{
public static void main(String[] args) throws IOException {
   // create ERD and user representations from JSON files
   String path = "./src/test/resources/output.json";
   File erdFile = new File("./src/test/resources/sales_order_model.json");
   File urFile = new File("./src/test/resources/sales_order_user_representations.json");
   File configuration = new File("./src/test/resources/configuration.json");
   EntityRelationDiagram erd = new EntityRelationDiagramImporterJSON().createERDFromJSONFile(erdFile);
   UserRepresentationContainer userRepresentations = new UserRepresentationContainerImporterJSON()
       .createUserRepresentationContainerFromJSONFile(urFile);
   SolverConfiguration configurationData = new SolverConfigurationFactory().createConfigurationWithJSONFile(configuration);

   // build solver context (user representations are optional)
   ServiceCutterContext context = new ServiceCutterContextBuilder(erd)
       .withUserRepresentations(userRepresentations).withCustomSolverConfiguration(configurationData)
       .build();

   // generate service decompositions
   SolverResult result = new ServiceCutter(context).generateDecomposition();
   try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
       out.write(new ObjectMapper().writeValueAsString(result).toString());
   } catch (Exception e) {
       e.printStackTrace();
   }
   
}
}