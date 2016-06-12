/**
 * This file is part of Palmetto Web Application.
 *
 * Palmetto Web Application is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Palmetto Web Application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Palmetto Web Application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.palmetto.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

import org.aksw.palmetto.Coherence;
import org.aksw.palmetto.corpus.WindowSupportingAdapter;
import org.aksw.palmetto.webapp.config.PalmettoConfiguration;
import org.aksw.palmetto.webapp.config.RootConfig;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerSocketProcessor;
import org.simpleframework.transport.SocketProcessor;
import org.simpleframework.transport.connect.SocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PalmettoApplication implements Container {

    private static final Logger LOGGER = LoggerFactory.getLogger(PalmettoApplication.class);

    private static final String MAX_NUMBER_OF_WORDS_PROPERTY_KEY = "org.aksw.palmetto.webapp.resources.AbstractCoherenceResource.maxWords";
    private static final String PALMETTO_PATH = "/palmetto-webapp";
    private static final String PALMETTO_SERVICE_PATH = "service/";
    private static final String PALMETTO_INDEX_FILE = "index.html";
    private static final String PALMETTO_RESOURCE_FOLDER = "webapp/";

    // private static final String WORDS_REQUEST_PARAMETER_NAME = "words";
    private static final String WORD_SEPARATOR = " ";
    private static final int PALMETTO_SERVER_PORT = 7777;

    private static final String MISSING_WORD_PARAMETER_ERROR = "The parameter \"words\" is missing.";
    private static final String NOT_ENOUGH_WORDS_ERROR = "The parameter \"words\" must contain at least 2 words separated by the following charater: '"
            + WORD_SEPARATOR + "'.";

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        PalmettoApplication palmettoContainer = new PalmettoApplication();
        palmettoContainer.init();
        SocketProcessor palmettoServer = new ContainerSocketProcessor(palmettoContainer);
        SocketConnection connection = new SocketConnection(palmettoServer);
        SocketAddress address = new InetSocketAddress(PALMETTO_SERVER_PORT);
        connection.connect(address);
    }

    protected WindowSupportingAdapter luceneAdapter;
    // protected Coherence caCoherence;
    // protected Coherence cpCoherence;
    // protected Coherence cvCoherence;
    // protected Coherence npmiCoherence;
    // protected Coherence uciCoherence;
    // protected Coherence umassCoherence;
    protected Map<String, Coherence> coherences;
    protected int maxNumberOfWords;
    protected String tooManyWordsErrorMsg;

    public PalmettoApplication() {
        LOGGER.error("started...");
        try {
            maxNumberOfWords = PalmettoConfiguration.getInstance().getInt(MAX_NUMBER_OF_WORDS_PROPERTY_KEY);
            tooManyWordsErrorMsg = "This instance of Palmetto does not support more than " + maxNumberOfWords
                    + " words for a single topic.";
        } catch (Exception e) {
            String errormsg = "Couldn't load \"" + MAX_NUMBER_OF_WORDS_PROPERTY_KEY + "\" from properties. Aborting.";
            LOGGER.error(errormsg, e);
            throw new IllegalStateException(errormsg, e);
        }
    }

    public void init() throws Exception {
        luceneAdapter = RootConfig.createLuceneAdapter();
        coherences = RootConfig.createCoherences(luceneAdapter);
        // caCoherence = RootConfig.createCACoherence(luceneAdapter);
        // cpCoherence = RootConfig.createCPCoherence(luceneAdapter);
        // cvCoherence = RootConfig.createCVCoherence(luceneAdapter);
        // npmiCoherence = RootConfig.createNPMICoherence(luceneAdapter);
        // uciCoherence = RootConfig.createUCICoherence(luceneAdapter);
        // umassCoherence = RootConfig.createUMassCoherence(luceneAdapter);
    }

    public void close() {
        luceneAdapter.close();
    }

    // @RequestMapping(value = "ca")
    // public ResponseEntity<String> caService(@RequestParam(value = "words")
    // String words) {
    // LOGGER.info("CA words=\"" + words + "\".");
    // return calculate(words, caCoherence);
    // }
    //
    // @RequestMapping(value = "cp")
    // public ResponseEntity<String> cpService(@RequestParam(value = "words")
    // String words) {
    // LOGGER.info("CP words=\"" + words + "\".");
    // return calculate(words, cpCoherence);
    // }
    //
    // @RequestMapping(value = "cv")
    // public ResponseEntity<String> cvService(@RequestParam(value = "words")
    // String words) {
    // LOGGER.info("CV words=\"" + words + "\".");
    // return calculate(words, cvCoherence);
    // }
    //
    // @RequestMapping(value = "npmi")
    // public ResponseEntity<String> npmiService(@RequestParam(value = "words")
    // String words) {
    // LOGGER.info("NPMI words=\"" + words + "\".");
    // return calculate(words, npmiCoherence);
    // }
    //
    // @RequestMapping(value = "uci")
    // public ResponseEntity<String> uciService(@RequestParam(value = "words")
    // String words) {
    // LOGGER.info("UCI words=\"" + words + "\".");
    // return calculate(words, uciCoherence);
    // }
    //
    // @RequestMapping(value = "umass")
    // public ResponseEntity<String> umassService(@RequestParam(value = "words")
    // String words) {
    // LOGGER.info("UMass words=\"" + words + "\".");
    // return calculate(words, umassCoherence);
    // }

    // protected ResponseEntity<String> calculate(String words, Coherence
    // coherence) {
    // String array[] = words.split(WORD_SEPARATOR);
    // if (array.length > maxNumberOfWords) {
    // return new ResponseEntity<String>("The request contains too many words.
    // This service supports a maximum of "
    // + maxNumberOfWords + " words.", HttpStatus.BAD_REQUEST);
    // } else {
    // return new ResponseEntity<String>(
    // Double.toString(coherence.calculateCoherences(new String[][] { array
    // })[0]), HttpStatus.OK);
    // }
    // }

    @Override
    public void handle(Request req, Response resp) {
        try {
            String path = req.getAddress().getPath().toString();
            if (!path.startsWith(PALMETTO_PATH)) {
                resp.setStatus(Status.NOT_FOUND);
                return;
            }
            // remove the palmetto path
            path = path.substring(PALMETTO_PATH.length());
            // if there is no path remaining, send the index file
            if ((path.length() == 0) || (path.equals("/"))) {
                sendFile(resp, PALMETTO_INDEX_FILE);
                return;
            } else if (path.startsWith("/")) {
                // if
                path = path.substring(1);
            } else {
                // something has been appended directly to the palmetto
                // web app path without a separator
                resp.setStatus(Status.NOT_FOUND);
                return;
            }
            // if we have to caluclate something
            if (path.startsWith(PALMETTO_SERVICE_PATH)) {
                performService(req, resp, path.substring(PALMETTO_SERVICE_PATH.length()));
            } else {
                // it seems that a file has been requested. try to find and send
                // it.
                sendFile(resp, path);
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected Exception.", e);
            resp.setStatus(Status.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                resp.commit();
            } catch (IOException e) {
            }
            try {
                resp.close();
            } catch (IOException e) {
            }
        }
    }

    private void performService(Request req, Response resp, String path) {
        String words = req.getParameter("words");
        if (words == null) {
            resp.setStatus(Status.BAD_REQUEST);
            sendMessage(resp, MISSING_WORD_PARAMETER_ERROR);
            return;
        }
        String array[] = words.split(WORD_SEPARATOR);
        if (array.length < 2) {
            resp.setStatus(Status.BAD_REQUEST);
            sendMessage(resp, NOT_ENOUGH_WORDS_ERROR);
            return;
        }
        if (array.length > maxNumberOfWords) {
            resp.setStatus(Status.BAD_REQUEST);
            sendMessage(resp, tooManyWordsErrorMsg);
            return;
        }
        if (!coherences.containsKey(path)) {
            resp.setStatus(Status.BAD_REQUEST);
            StringBuilder builder = new StringBuilder();
            builder.append("The coherence \"");
            builder.append(path);
            builder.append("\" is not known. The following coherences are supported by this Palmetto instance ");
            builder.append(coherences.keySet().toString());
            sendMessage(resp, builder.toString());
            return;
        }
        double coherence[] = coherences.get(path).calculateCoherences(new String[][] { array });
        resp.setStatus(Status.OK);
        sendMessage(resp, Double.toString(coherence[0]));
    }

    private void sendFile(Response resp, String resource) {
        if (resource.startsWith(PALMETTO_INDEX_FILE)) {
            sendFile(resp, resource, "text/html; charset=utf-8");
        } else if (resource.startsWith("css")) {
            sendFile(resp, resource, "text/css; charset=utf-8");
        } else if (resource.startsWith("js")) {
            sendFile(resp, resource, "application/javascript; charset=utf-8");
        } else if (resource.startsWith("images")) {
            sendFile(resp, resource, "image/png");
        } else {
            sendFile(resp, resource, null);
        }
    }

    private void sendFile(Response resp, String resource, String type) {
        InputStream resourceStream = null;
        OutputStream output = null;
        try {
            resourceStream = this.getClass().getClassLoader().getResourceAsStream(PALMETTO_RESOURCE_FOLDER + resource);
            if (resourceStream == null) {
                resp.setStatus(Status.NOT_FOUND);
                return;
            }

            output = resp.getOutputStream();
            resp.setStatus(Status.OK);
            if (type != null) {
                resp.setValue(Protocol.CONTENT_TYPE, type);
            }
            int length = IOUtils.copy(resourceStream, output);
            resp.setContentLength(length);
        } catch (Exception e) {
            resp.setStatus(Status.INTERNAL_SERVER_ERROR);
        } finally {
            IOUtils.closeQuietly(resourceStream);
            IOUtils.closeQuietly(output);
        }
    }

    private void sendMessage(Response resp, String message) {
        OutputStream output = null;
        try {
            resp.setValue(Protocol.CONTENT_TYPE, "text/plain; charset=utf-8");
            output = resp.getOutputStream();
            byte msgBytes[] = message.getBytes(Charsets.UTF_8);
            resp.setContentLength(msgBytes.length);
            output.write(msgBytes);
        } catch (Exception e) {
            resp.setStatus(Status.INTERNAL_SERVER_ERROR);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

}
