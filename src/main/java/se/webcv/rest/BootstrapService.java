package se.webcv.rest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

@Controller
@RequestMapping("/bootstrap")
public class BootstrapService {

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public BootResult boot() {
        return new BootResult(System.getProperty("google.auth.clientId", "853597222975-dpjpp1tselrldr1dmg9qj95pd5dar7e7.apps.googleusercontent.com"), null);
    }

    public static class AtomLink {
        public final String rel;
        public final String href;

        public AtomLink(String rel, String href) {
            this.rel = rel;
            this.href = href;
        }
    }

    public static class BootResult {
        public final String clientId;
        public final Collection<AtomLink> resources;

        public BootResult(String clientId, Collection<AtomLink> resources) {
            this.clientId = clientId;
            this.resources = resources;
        }
    }
}
