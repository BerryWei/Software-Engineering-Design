import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // File inputFile = new File(args[0]); // 從命令行參數獲取檔案名
            File inputFile = new File(args[0]); // 從命令行參數獲取檔案名
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int monitorPeriod = Integer.parseInt(line.trim()); // 將監控週期轉換為整數
            // System.out.println("Monitor Period: " + monitorPeriod);

            // 讀取患者和設備資訊
            ArrayList<Patient> patients = new ArrayList<>();
            String patientInfo = null;
            Sensor bloodPressureSensor = null;
            Sensor pulseSensor = null;
            Sensor temperatureSensor = null;

            while ((line = reader.readLine()) != null) { // 確保不會讀取空行
                line = line.trim(); // 移除行首尾的空白

                if (line.startsWith("patient")) {
                    // System.out.println("Patient Info: " + line);

                    if (patientInfo != null) {
                        Patient patient = Patient.makePatient(patientInfo, pulseSensor, bloodPressureSensor,
                                temperatureSensor);
                        patients.add(patient);
                        // 重置所有变量以开始新的患者记录
                        bloodPressureSensor = null;
                        pulseSensor = null;
                        temperatureSensor = null;
                    }
                    patientInfo = line;

                } else if (line.startsWith("BloodPressureSensor")) {
                    bloodPressureSensor = Sensor.makeSensor(line);
                } else if (line.startsWith("PulseSensor")) {
                    pulseSensor = Sensor.makeSensor(line);
                } else if (line.startsWith("TemperatureSensor")) {
                    temperatureSensor = Sensor.makeSensor(line);
                }
            }
            reader.close();
            // add last patient
            Patient patient = Patient.makePatient(patientInfo, pulseSensor, bloodPressureSensor, temperatureSensor);
            patients.add(patient);
            patient = null;
            // for debug
            // for (Patient pat : patients) {
            // System.out.println(pat.toString());
            // }

            // start to print

            for (int time = 0; time <= monitorPeriod; time++) {
                for (Patient pate : patients) {
                    int patientPeriod = pate.patientPeriod;
                    if (time % patientPeriod == 0) {

                        // pate.bloodPressureSensor
                        var bloodPressureSensoTemp = pate.bloodPressureSensor;
                        var pulseSensorTemp = pate.pulseSensor;
                        var temperatureSensorTemp = pate.temperatureSensor;

                        if (bloodPressureSensoTemp != null) {
                            int idx = bloodPressureSensoTemp.index;
                            var value_bloodPressure = bloodPressureSensoTemp.dataSetValues.get(idx);
                            boolean isDanger_bloodPressure = bloodPressureSensoTemp
                                    .isDanger((double) value_bloodPressure);
                            if (value_bloodPressure == -1) {
                                System.out
                                        .println("[" + time + "]" + " " + bloodPressureSensoTemp.sensorName + " fails");
                            } else if (isDanger_bloodPressure) {
                                System.out.println("[" + time + "]" + " " +
                                        pate.patientName + " is in danger! Cause: " + bloodPressureSensoTemp.sensorName
                                        + " " + value_bloodPressure);
                            }
                            // index ++
                            bloodPressureSensoTemp.indexPlusPlus();

                        }

                        if (pulseSensorTemp != null) {
                            int idx = pulseSensorTemp.index;
                            var value_pulse = pulseSensorTemp.dataSetValues.get(idx);
                            boolean isDanger_pulse = pulseSensorTemp.isDanger((double) value_pulse);
                            if (value_pulse == -1) {
                                System.out.println("[" + time + "]" + " " + pulseSensorTemp.sensorName + " fails");
                            } else if (isDanger_pulse) {
                                System.out.println("[" + time + "]" + " " +
                                        pate.patientName + " is in danger! Cause: " + pulseSensorTemp.sensorName + " "
                                        + value_pulse);
                            }
                            pulseSensorTemp.indexPlusPlus();
                        }

                        if (temperatureSensorTemp != null) {
                            int idx = temperatureSensorTemp.index;
                            var value_temperature = temperatureSensorTemp.dataSetValues.get(idx);
                            boolean isDanger_temperature = temperatureSensorTemp.isDanger((double) value_temperature);
                            if (value_temperature == -1) {
                                System.out
                                        .println("[" + time + "]" + " " + temperatureSensorTemp.sensorName + " fails");
                            } else if (isDanger_temperature) {
                                System.out.println("[" + time + "]" + " " +
                                        pate.patientName + " is in danger! Cause: " + temperatureSensorTemp.sensorName
                                        + " " + value_temperature);
                            }
                            temperatureSensorTemp.indexPlusPlus();
                        }
                    }
                }
            } // end time forloop

            // print patient info for each
            for (Patient pate : patients) {
                System.out.println("patient " + pate.patientName);

                var bloodPressureSensoTemp = pate.bloodPressureSensor;
                var pulseSensorTemp = pate.pulseSensor;
                var temperatureSensorTemp = pate.temperatureSensor;
                if (bloodPressureSensoTemp != null) {
                    System.out.println("BloodPressureSensor " + bloodPressureSensoTemp.sensorName);
                    bloodPressureSensoTemp.printByTime(monitorPeriod, pate.patientPeriod);
                }

                if (pulseSensorTemp != null) {
                    System.out.println("PulseSensor " + pulseSensorTemp.sensorName);
                    pulseSensorTemp.printByTime(monitorPeriod, pate.patientPeriod);
                }

                if (temperatureSensorTemp != null) {
                    System.out.println("TemperatureSensor " + temperatureSensorTemp.sensorName);
                    temperatureSensorTemp.printByTime(monitorPeriod, pate.patientPeriod);
                }
            }

        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Invalid format for monitor period.");
            e.printStackTrace();
        }
    }
}

class Patient {
    public String patientName;
    public int patientPeriod;
    public Sensor pulseSensor;
    public Sensor bloodPressureSensor;
    public Sensor temperatureSensor;

    public Patient(String name, int patientPeriod, Sensor pulseSensor, Sensor bloodPressureSensor,
            Sensor temperatureSensor) {
        this.patientName = name;
        this.patientPeriod = patientPeriod;
        this.pulseSensor = pulseSensor;
        this.bloodPressureSensor = bloodPressureSensor;
        this.temperatureSensor = temperatureSensor;
    }

    public static Patient makePatient(String input, Sensor pulseSensor, Sensor bloodPressureSensor,
            Sensor temperatureSensor) {
        String[] parts = input.split(" "); // 使用空格分割字符串
        // 確保 parts 有足夠的部分
        if (parts.length < 3)
            return null; // 或者拋出一個異常

        String name = parts[1]; // 這將會是 "Mark"
        int value = Integer.parseInt(parts[2]); // 將字符串轉換為 int
        return new Patient(name, value, pulseSensor, bloodPressureSensor, temperatureSensor); // 使用提供的值創建 Patient 實例
    }

    @Override
    public String toString() {
        String result = "Patient Name: " + patientName + ", Monitoring Period: " + patientPeriod;
        if (pulseSensor != null) {
            result += ", Pulse Sensor: [" + pulseSensor.toString() + "]";
        }
        if (bloodPressureSensor != null) {
            result += ", Blood Pressure Sensor: [" + bloodPressureSensor.toString() + "]";
        }
        if (temperatureSensor != null) {
            result += ", Temperature Sensor: [" + temperatureSensor.toString() + "]";
        }
        return result;
    }

}

class Sensor {
    public String sensorName;
    public String dataSetName;
    public double lower;
    public double upper;
    public List<Double> dataSetValues;
    public int index = 0;

    public Sensor(String sensorName, String dataSetName, double lower, double upper) {
        this.sensorName = sensorName;
        this.dataSetName = dataSetName;
        this.lower = lower;
        this.upper = upper;
        this.dataSetValues = new ArrayList<>(); // 在调用 loadDataSetValues 之前初始化 dataSetValues
        loadDataSetValues();
    }

    public static Sensor makeSensor(String input) {
        // "BloodPressureSensor sensor1 BloodPressureData1.dataset 150 200"
        String[] parts = input.split(" ");
        String type = parts[0];
        String name = parts[1];
        String dataset = parts[2];
        double lower = Double.parseDouble(parts[3]);
        double upper = Double.parseDouble(parts[4]);

        return new Sensor(name, dataset, lower, upper);
    }

    private void loadDataSetValues() {
        this.dataSetValues = new ArrayList<>(); // 确保列表被初始化
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataSetName)); // 使用 dataSetName 作为文件名
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    double value = Double.parseDouble(line.trim()); // 将读取的行转换为 double
                    this.dataSetValues.add(value); // 将转换后的数值添加到列表中
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing dataset value: " + line);
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Error reading dataset values: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Name: " + sensorName + ", Dataset: " + dataSetName + ", Range: [" + lower + ", " + upper + "]";
    }

    public boolean isDanger(double value) {
        boolean bool = false;
        if (value < this.lower || value > upper) {
            bool = true;
        }
        return bool;
    }

    public void indexPlusPlus() {
        int length = this.dataSetValues.size();
        int newIndex = index + 1;
        if (newIndex < length) {
            this.index = newIndex;
        } else {
            this.index = length - 1;
        }

    }

    public void printByTime(int endTime, int patientPeriod) {
        this.index = 0;
        for (int time = 0; time <= endTime; time++) {
            if (time % patientPeriod == 0) {
                int idx = this.index;
                var value_bloodPressure = this.dataSetValues.get(idx);
                System.out.println("[" + time + "]" + " " + value_bloodPressure);
                indexPlusPlus();
            }
        }
    }

}