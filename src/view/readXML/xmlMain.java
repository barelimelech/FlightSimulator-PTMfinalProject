package view.readXML;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;


public class xmlMain {

    static String ip, port;
    public static ArrayList<myProperty> list = new ArrayList<>();

    public static ArrayList<myProperty> getPropertyList() {
        BufferedReader br = null;
        String line = null;
        ArrayList<String> array = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader("properties.csv"));
            while ((line = br.readLine()) != null) {
                String[] s = line.split(",");
                array.add(s[0]);
                array.add(s[1]);
            }

            br.close();

            ip = array.get(1);
            port = array.get(3);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayList<myProperty> list = new ArrayList<>();


        int i = 0;
        for (String name : array) {
            if (i >= 4 && i % 2 == 0)
                list.add(new myProperty(name));
            i++;
        }
        return list;
    }

    public static void WriteToXML(ArrayList<myProperty> propList) {
        try {
            FileOutputStream fos = new FileOutputStream("./XML Files/propertiesXML.xml");
            XMLEncoder encoder = new XMLEncoder(fos);

            encoder.setExceptionListener(new ExceptionListener() {
                public void exceptionThrown(Exception e) {
                    System.out.println("Exception! :" + e.toString());
                }
            });

            encoder.writeObject("ip");
            encoder.writeObject(ip);
            encoder.writeObject("port");
            encoder.writeObject(port);
            for (myProperty mp : propList) {
                encoder.writeObject(mp);
            }

            encoder.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<myProperty> ReadFromXML(int numOfFeatures) {
        try {
            FileInputStream fis = new FileInputStream("./XML Files/propertiesXML.xml");
            XMLDecoder decoder = new XMLDecoder(fis);

            ArrayList<myProperty> listtt = new ArrayList<>();
            myProperty prop;

            decoder.readObject();
            String ip = (String) decoder.readObject();
            decoder.readObject();
            String port = (String) decoder.readObject();

            while (numOfFeatures != 0) {
                prop = (myProperty) decoder.readObject();
                listtt.add(prop);
                numOfFeatures--;
            }

            decoder.close();
            fis.close();

            return listtt;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        list = getPropertyList();
        WriteToXML(list);
        list = ReadFromXML(list.size());
    }

}

