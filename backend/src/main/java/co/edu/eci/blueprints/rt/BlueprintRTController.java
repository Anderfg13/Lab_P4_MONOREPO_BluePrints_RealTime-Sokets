package co.edu.eci.blueprints.rt;

import co.edu.eci.blueprints.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
@Validated
public class BlueprintRTController {

  private static final Logger log = LoggerFactory.getLogger(BlueprintRTController.class);

  private final SimpMessagingTemplate template;

  public BlueprintRTController(SimpMessagingTemplate template) {
    this.template = template;
  }

  @MessageMapping("/draw")
  public void onDraw(@Valid DrawEvent evt) {
    String topic = "/topic/blueprints." + evt.author() + "." + evt.name();
    log.info("RT event draw received author={} name={} point=({}, {})", evt.author(), evt.name(), evt.point().getX(), evt.point().getY());
    var upd = new BlueprintUpdate(evt.author(), evt.name(), List.of(evt.point()));
    template.convertAndSend(topic, upd);
    log.debug("RT update sent topic={} points={}", topic, upd.points().size());
  }

  @ResponseBody
  @GetMapping("/api/rt/blueprints/{author}/{name}")
  public BlueprintUpdate get(@PathVariable @NotBlank String author, @PathVariable @NotBlank String name) {
    return new BlueprintUpdate(author, name, List.of(new PointDTO(10,10), new PointDTO(40,50)));
  }
}
