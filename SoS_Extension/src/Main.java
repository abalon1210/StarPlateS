import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        boolean isSMBFL = false;
        boolean isBMBFL = false;
        boolean isIMBFL = false;
        boolean isClustering = false;
        boolean withSim = true;

        if (args.length == 0) {
            System.out.println("Usage: java main <switch> <withSim> [<file>]");
            System.out.println("Where: <switch> = -structure or -smbfl");
            System.out.println("                  -behavior or -bmbfl");
            System.out.println("                  -interplay or -imbfl");
            System.out.println("                  -clustering or -cl");
            System.out.println("                  -all");
            System.out.println("       <withSim> = -simon (default)");
            System.out.println("                   -simoff");
            System.exit(1);
        }

        if (args[0].equals("-structure") || args[0].equals("-smbfl"))
            isSMBFL = true;
        else if (args[0].equals("-behavior") || args[0].equals("-bmbfl"))
            isBMBFL = true;
        else if (args[0].equals("-interplay") || args[0].equals("-imbfl"))
            isIMBFL = true;
        else if (args[0].equals("-clustering") || args[0].equals("-cl"))
            isClustering = true;
        else if (args[0].equals("-all")) {
            isSMBFL = true;
            isBMBFL = true;
            isIMBFL = true;
            isClustering = true;
        }

        if(args[1].equals("-simoff")) {
            withSim = false;
        }

        int numScenario = 150;                                          // TODO The number of scenarios generated
        int numRepeat = 1;                                              // TODO The number of repetition of the same scenario for statistical model checking

        StructureModelBasedFaultLocalization smbfl = new StructureModelBasedFaultLocalization();
//        BehaviorModelBasedFaultLocalization bmfl                      // TODO create new class
        InterplayModelBasedFaultLocalization imbfl = new InterplayModelBasedFaultLocalization();

        if(withSim) {
            // Generate Random Scenario with Scenario Generation Module
            ScenarioGenerator scenarioGenerator = new ScenarioGenerator();
            scenarioGenerator.generateRandomScenario(numScenario);      // TODO Call the scenario generation module with the number of scenarios

            SimulationExecutor simulationExecutor = new SimulationExecutor();
            simulationExecutor.run(numScenario, numRepeat, isSMBFL, isBMBFL, isIMBFL, smbfl, imbfl); // TODO add more configuration params, eventDuration, etc
        }

        Verifier verifier = new Verifier();
        int[] thresholds = {80};                                        // TODO Threshold value for the Verirfication Property 1
        int[] thresholds2 = {4};                                        // TODO Threshold value for the VP2
        String base = System.getProperty("user.dir");
        System.out.println(System.getProperty("user.dir"));
        int matchingtxts = 0;
        String currentdir = base + "/SoS_Extension/logs/";
        System.out.print("Current Working Directory : " + currentdir +"\n");
        File f = new File(currentdir);
        Boolean result;
        matchingtxts = 0;

        ArrayList<InterplayModel> IMs = new ArrayList<>();

        if(f.exists()){
            int numoffiles = f.listFiles().length + 300;
            System.out.println("and it has " + numoffiles + " files.");
            for (int i = 0; i < numoffiles; i++){
                String txtdir = currentdir + Integer.toString(i) + "_0plnData.txt";
                File temptxt = new File(txtdir);
                if(temptxt.exists()){
                    matchingtxts++;
                    for (int thshold : thresholds){
                        result = verifier.verifyLog(txtdir,"operationSuccessRate", thshold);
                        if(!result) {
                            InterplayModel interplayModel = new InterplayModel(i, 0);                        // TODO r_index = 0 로 설정해놓음
//                            clustering.addTrace(interplayModel, simlr_threshold);                                  // TODO Similarity Threshold = 75%
                            IMs.add(interplayModel);
                        }
                    }
//                        for (int thshold2 : thresholds2){
////                           System.out.println("opreation Time" + thshold2);
//                            result = verifier.verifyLog(txtdir, nof, "operationTime", thshold2);
//                            smbfl.structureModelOverlapping(results, i, 0);
//                        }
                }
            }
        } else {
            System.out.println("There is no such directory");
        }
        System.out.println("There were " + matchingtxts + " platooning text files");
        //}
//
        if (isSMBFL) {                                                  // Structure Model-based Fault Localization
            ArrayList<EdgeInfo> edgeInfos = smbfl.SMcalculateSuspiciousness();
            StructureModel finalSM = new StructureModel();
            finalSM.collaborationGraph = smbfl.overlappedG;
//            finalSM.drawGraph();
            System.out.println(edgeInfos.size());
            File file2 = new File(System.getProperty("user.dir") + "/examples/platoon_SoS/results/SBFL_result.csv");
            FileWriter writer2 = null;

            for (EdgeInfo edgeInfo: edgeInfos) {
                System.out.println("name: "+edgeInfo.edge+",    pass: "+edgeInfo.pass+",    fail: "+edgeInfo.fail+",    tarantula: "+edgeInfo.tarantulaM+", ochiai: "+edgeInfo.ochiaiM + ", op2: "+edgeInfo.op2M + ",   barinel: "+edgeInfo.barinelM+", dstar: "+edgeInfo.dstarM);
                try {
                    writer2 = new FileWriter(file2, true);
                    writer2.write(edgeInfo.edge+ "," + edgeInfo.pass+ "," + edgeInfo.fail + "," + edgeInfo.tarantulaM + "," + edgeInfo.ochiaiM+ "," + edgeInfo.op2M+ "," + edgeInfo.barinelM+ "," + edgeInfo.dstarM+"\n");
                    writer2.flush();
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(writer2 != null) writer2.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (NodeInfo nodeInfo: smbfl.nodeInfos) {
                System.out.println("name: "+nodeInfo.node+",    pass: "+nodeInfo.pass+",    fail: "+nodeInfo.fail);
                try {
                    writer2 = new FileWriter(file2, true);
                    writer2.write(nodeInfo.node+ "," + nodeInfo.pass+ "," + nodeInfo.fail +"\n");
                    writer2.flush();
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(writer2 != null) writer2.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
//
//        if (isBMBFL) {
//
//        }
//
        if (isIMBFL) {                                                 // Interaction Model-based Fault Localization
            imbfl.printSuspSequences();
        }

        ArrayList<ArrayList<String>> oracle = new ArrayList<>();
        oracle.add(new ArrayList<>(Arrays.asList("6_0","7_0","8_0","9_0","11_0","13_0","41_0","47_0")));
        oracle.add(new ArrayList<>(Arrays.asList("3_0","6_0","12_0","46_0")));
        oracle.add(new ArrayList<>(Arrays.asList("17_0","30_0","45_0","49_0")));
        oracle.add(new ArrayList<>(Arrays.asList("22_0","24_0","29_0","38_0","46_0")));
        oracle.add(new ArrayList<>(Arrays.asList("24_0","27_0","29_0","34_0","38_0","47_0")));
        oracle.add(new ArrayList<>(Arrays.asList("43_0")));

        double simlr_threshold;
        double delay_threshold;
        int lcs_min_len_threshold;
        double evaluation_score;
        boolean single = true;

        if(isClustering && !single) {
            File file2 = new File(base + "/SoS_Extension/" + "HyperparameterAnalysis.csv");

            try {
                FileWriter writer = new FileWriter(file2, true);
                for(simlr_threshold = 0.5; simlr_threshold <= 1.0; simlr_threshold+=0.01) {
                    for(delay_threshold = 0.1; delay_threshold <= 1.5; delay_threshold+=0.1) {
                        for(lcs_min_len_threshold = 5; lcs_min_len_threshold <= 20; lcs_min_len_threshold++) {
                            Clustering clustering = new Clustering();

                            for (InterplayModel im : IMs) {
                                clustering.addTraceCase5(im, simlr_threshold, delay_threshold, lcs_min_len_threshold);
                            }
                            clustering.ClusteringFinalize(simlr_threshold, delay_threshold, lcs_min_len_threshold);
                            evaluation_score = clustering.EvaluateClusteringResult(oracle);
                            System.out.println("Clustering Evaluation Score: " + evaluation_score);
                            writer.write(simlr_threshold + "," + delay_threshold + "," + lcs_min_len_threshold + "," + evaluation_score);
                            writer.write("\n");
//                            clustering.printCluster();
//                            clustering.clusterClear();
                        }
                    }
                }
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Clustering clustering = new Clustering();
            simlr_threshold = 0.7;
            delay_threshold = 1.0;
            lcs_min_len_threshold = 15;

            for (InterplayModel im : IMs) {
                clustering.addTraceCase6(im, simlr_threshold, delay_threshold, lcs_min_len_threshold);
            }
//            clustering.ClusteringFinalize(simlr_threshold, delay_threshold, lcs_min_len_threshold);
            evaluation_score = clustering.EvaluateClusteringResult(oracle);
            System.out.println("Clustering Evaluation Score: " + evaluation_score);
//            clustering.printCluster();
//            clustering.clusterClear();
        }
    }
}
