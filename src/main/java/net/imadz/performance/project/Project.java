package net.imadz.performance.project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by geek on 8/17/14.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Project {

    public static Project read(final String projectFilePath) {
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance(Project.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setProperty("eclipselink.media-type", "application/json");
            unmarshaller.setProperty("eclipselink.json.include-root", false);
            final StreamSource source = new StreamSource(new FileReader(projectFilePath));
            JAXBElement<Project> jaxbElement = unmarshaller.unmarshal(source, Project.class);
            return jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void write(final String projectFilePath, final Project updated) {
        JAXBContext jc = null;
        try {
            jc = JAXBContext.newInstance(Project.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty("eclipselink.media-type", "application/json");
            marshaller.setProperty("eclipselink.json.include-root", false);
            marshaller.marshal(updated, new File(projectFilePath));
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }


    private String name;
    private String[] resourceTypes;
    private Reader[] readers;
    private Painter[] painters;
}
