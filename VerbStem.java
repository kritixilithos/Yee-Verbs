import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

class VerbStem {
	public static void main(String[] args) throws Exception {
		URL site;
		try {
			site = new URL("http://verbmaps.com/en/verb/"+args[2]+"/"+args[0]);
			URLConnection yc = site.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
						yc.getInputStream()));
			String inputLine;
			String content="";
			while ((inputLine = in.readLine()) != null) 
				content += inputLine;
			in.close();
	
			content = content.replaceAll("\\s","");
			Pattern p = Pattern.compile("(?<=>)([^><]+)(?=<\\/span><\\/div><divclass=\"transform\">[^>]*>Add"+args[1]+")",Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(content);
			while (matcher.find()) {
				System.out.println(matcher.group());
			}
		}
		catch(Exception e) {
			System.err.println("Error 404");
		}
	}
}
