package epfl.pacman.maze.view

import javax.swing.JComponent //, JFrame}
import javax.imageio.ImageIO
import java.io.File
import java.awt.{Graphics, Graphics2D, Color } // , Dimension}
import swing._

import epfl.pacman.maze.model._

class View(initModel: Model) extends Component {
  object Settings {
    val borderWidth = 2

    val vBlocks = 20
    val hBlocks = 30

    val blockWidth  = 30
    val blockHeight = 30

    val width = vBlocks * blockWidth + 2 * borderWidth
    val height = hBlocks * blockHeight + 2 * borderWidth
  }

  import Settings._

  var model = initModel

  override lazy val peer = new JComponent() {
    override def paintComponent(graph: Graphics) {
      println("Painting Comp..")
      val g = graph.asInstanceOf[Graphics2D]

      g.setColor(Color.BLACK)

      for (w <- model.walls) {
        drawWall(w, g)
      }

      for (m <- model.monsters) {
        drawMonster(m, g)
      }

      drawPacman(model.pacman, g)
    }
  }

  def repaint(newModel: Model) {
    model = newModel
    repaint()
  }

  case class AbsolutePosition(x: Int, y: Int)

  def absPos(p: Position): AbsolutePosition =
    AbsolutePosition(p.x*blockWidth+borderWidth, p.y*blockHeight+borderWidth)


  def drawPacman(p: Pacman, g: Graphics2D) = {
    g.setColor(Color.YELLOW)

    val radius = blockWidth/2-5
    val centerX = absPos(p.pos).x+blockWidth/2
    val centerY = absPos(p.pos).y+blockWidth/2

    val angle = 30

    val startAngle = p.dir match {
      case Left  => 180 + angle
      case Right => 0 + angle
      case Down  => 270 + angle
      case Up    => 90 + angle
    }

    g.fillArc(centerX - radius, centerY - radius, 2 * radius, 2 * radius, startAngle, 360-2*angle);
  }

  def drawWall(w: Wall, g: Graphics2D) = {
    // Based on the walls around, draw differently
    g.setColor(Color.CYAN)
    val x = absPos(w.pos).x
    val y = absPos(w.pos).y

    var lborder = 0;
    var rborder = 0;
    var tborder = 0;
    var bborder = 0;

    if (!model.isWallAt(w.pos.onTop)) {
      g.drawPolyline(Array(x, x+blockWidth), Array(y, y), 2)
      tborder = 5
    }

    if (!model.isWallAt(w.pos.onBottom)) {
      g.drawPolyline(Array(x, x+blockWidth), Array(y+blockHeight, y+blockHeight), 2)
      bborder = 5
    }

    if (!model.isWallAt(w.pos.onLeft)) {
      g.drawPolyline(Array(x, x), Array(y, y+blockHeight), 2)
      lborder = 3
    }

    if (!model.isWallAt(w.pos.onRight)) {
      g.drawPolyline(Array(x+blockWidth, x+blockWidth), Array(y, y+blockHeight), 2)
      rborder = 3
    }

    g.setColor(Color.BLUE)
    for (i <- tborder to blockHeight-bborder by 5) {
      g.drawPolyline(Array(x+lborder, x+blockWidth-rborder), Array(y + i, y + i), 2)
    }
  }

  val image = ImageIO.read(new File("src/main/resources/badguy1.png"))

  def drawMonster(m: Monster, g: Graphics2D) = {
    g.drawImage(image, absPos(m.pos).x, absPos(m.pos).y+2, null)
  }

}
