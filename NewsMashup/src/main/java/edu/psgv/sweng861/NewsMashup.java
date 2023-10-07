package edu.psgv.sweng861;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


public class NewsMashup {
	
	
	
	public static void main(String[] args) {
	
		try {
			  nyNews();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	
		
		
		try {
			londonWeather();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		nbaSchedule();
		
	}
	

	private static void nbaSchedule() {
		String url = "https://www.foxsports.com/nfl/dallas-cowboys-team-schedule";
		
        try {
            // Fetch the HTML content from the URL
            org.jsoup.nodes.Document document = Jsoup.connect(url).get();
            
            System.out.println("Dallas Cowboys NBA Schedule (unformatted):");

            Elements data = ((Element) document).select("tbody.row-data.lh-1pt43.fs-14");
            
            System.out.println(data);
		    System.out.println("-------------------------------------------------------------------------------------");
		    System.out.println("Dallas Cowboys NBA Schedule (formatted):");
            
            for(Element e: data.select("tr")){
				String dateLocation = e.select("td.cell-text.ff-h div").text();
				System.out.print("On " + dateLocation + ": Opponent: ");
				String opponents = e.select("td.cell-entity.fs-18.lh-1pt67 div").text();
				System.out.print(opponents + " at");
				String timeChannel = e.select("td.cell-time.broadcast.ff-h div").text();
				System.out.println(timeChannel);
			}
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}


	private static void londonWeather() throws IOException, InterruptedException {
		
		 HttpClient client = HttpClient.newHttpClient();
		    HttpRequest request = HttpRequest.newBuilder()
		          .uri(URI.create("http://api.openweathermap.org/data/2.5/weather?q=London,GB&appid=6ddf1159d4a9130f6e79444d0cda0bbb&units=imperial"))
		          .build();

		    HttpResponse<String> response =
		          client.send(request, BodyHandlers.ofString());

		    System.out.println("London Weather (unformatted):");
		    System.out.println(response.body());
		    System.out.println("-------------------------------------------------------------------------------------");
		    System.out.println("London Weather (formatted):");
		    
		    //HttpResponse<String> jsonResponse = response;

	        try {
	            // Parse the JSON response
	            JSONObject jsonObject = new JSONObject(response.body());

	            // Extract weather information
	            double temperature = jsonObject.getJSONObject("main").getDouble("temp");
	            double pressure = jsonObject.getJSONObject("main").getDouble("pressure");
	            double humidity = jsonObject.getJSONObject("main").getDouble("humidity");
	            String weatherDescription = jsonObject.getJSONArray("weather")
	                    .getJSONObject(0).getString("description");

	            // Print extracted weather information
	            System.out.println("Temperature: " + temperature + " K");
	            System.out.println("Humidity: " + humidity + "%");
	            System.out.println("Pressure: " + pressure + " mbar");
	            System.out.println("Weather Description: " + weatherDescription);
	            System.out.println("-------------------------------------------------------------------------------------");
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		    
		    
	}


	private static void nyNews() throws IOException, InterruptedException {
			
		try {
            // Create a URL object with the API endpoint
            URL url = new URL("https://rss.nytimes.com/services/xml/rss/nyt/NYRegion.xml");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();

            // If the response code is 200, read the XML response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the XML response
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = (Document) builder.parse(url.openStream());
                
                System.out.println("NY news (unformatted): ");
                System.out.println(response);
                System.out.println("-------------------------------------------------------------------------------------");
                System.out.println("NY news (formatted): ");

                // Get the <item> nodes from the XML
                NodeList itemList = ((org.w3c.dom.Document) document).getElementsByTagName("item");

                // Iterate through the <item> nodes and print the titles
                for (int i = 0; i < itemList.getLength(); i++) {
                	System.out.println("NEWS Count: "+ (i +1));
                    Node item = itemList.item(i);
                    NodeList childNodes = item.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if ("title".equals(childNode.getNodeName())) {
                            System.out.println("Title: " + childNode.getTextContent());
                            System.out.println("");
                        }
                    }
                    //System.out.println("__________________________________________");;
                }
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
            System.out.println("-------------------------------------------------------------------------------------");


        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
	}

}

