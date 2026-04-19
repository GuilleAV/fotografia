from pathlib import Path
import argparse
import re
from datetime import datetime
from xml.sax.saxutils import escape

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_LEFT, TA_CENTER
from reportlab.lib import colors
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak


ROOT = Path(__file__).resolve().parent
DEFAULT_INPUT_MD = ROOT / "MANUAL_USUARIO_SISTEMA.md"
DEFAULT_OUTPUT_PDF = ROOT / "MANUAL_USUARIO_SISTEMA.pdf"


def draw_page_chrome(canvas, doc, title: str, subtitle: str) -> None:
    canvas.saveState()

    width, height = A4
    accent = colors.HexColor("#c6933f")
    dark = colors.HexColor("#1a1815")
    muted = colors.HexColor("#756f63")

    canvas.setStrokeColor(accent)
    canvas.setLineWidth(1)
    canvas.line(doc.leftMargin, height - 24, width - doc.rightMargin, height - 24)

    canvas.setFont("Helvetica-Bold", 9)
    canvas.setFillColor(dark)
    canvas.drawString(doc.leftMargin, height - 18, "SENTIR FOTOGRÁFICO")

    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(muted)
    canvas.drawRightString(width - doc.rightMargin, height - 18, subtitle)

    canvas.setStrokeColor(colors.HexColor("#ddd5c6"))
    canvas.line(doc.leftMargin, 26, width - doc.rightMargin, 26)
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(muted)
    canvas.drawString(doc.leftMargin, 14, title)
    canvas.drawRightString(width - doc.rightMargin, 14, f"Página {canvas.getPageNumber()}")

    canvas.restoreState()


def build_pdf(md_text: str, output_path: Path, title: str, author: str, subtitle: str) -> None:
    styles = getSampleStyleSheet()

    title_style = ParagraphStyle(
        "ManualTitle",
        parent=styles["Title"],
        fontName="Helvetica-Bold",
        fontSize=22,
        leading=26,
        alignment=TA_CENTER,
        textColor=colors.HexColor("#1a1815"),
        spaceAfter=14,
    )

    h1_style = ParagraphStyle(
        "ManualH1",
        parent=styles["Heading1"],
        fontName="Helvetica-Bold",
        fontSize=15,
        leading=20,
        alignment=TA_LEFT,
        textColor=colors.HexColor("#1a1815"),
        spaceBefore=12,
        spaceAfter=8,
    )

    h2_style = ParagraphStyle(
        "ManualH2",
        parent=styles["Heading2"],
        fontName="Helvetica-Bold",
        fontSize=12,
        leading=16,
        alignment=TA_LEFT,
        textColor=colors.HexColor("#1a1815"),
        spaceBefore=8,
        spaceAfter=5,
    )

    normal_style = ParagraphStyle(
        "ManualNormal",
        parent=styles["BodyText"],
        fontName="Helvetica",
        fontSize=10.5,
        leading=14,
        textColor=colors.HexColor("#2a2824"),
        spaceAfter=4,
    )

    bullet_style = ParagraphStyle(
        "ManualBullet",
        parent=normal_style,
        leftIndent=14,
        bulletIndent=0,
    )

    cover_title_style = ParagraphStyle(
        "CoverTitle",
        parent=title_style,
        fontName="Helvetica-Bold",
        fontSize=24,
        leading=30,
        textColor=colors.HexColor("#1a1815"),
        spaceAfter=12,
    )

    cover_subtitle_style = ParagraphStyle(
        "CoverSubtitle",
        parent=styles["Heading2"],
        fontName="Helvetica",
        fontSize=12,
        leading=16,
        alignment=TA_CENTER,
        textColor=colors.HexColor("#756f63"),
        spaceAfter=8,
    )

    cover_meta_style = ParagraphStyle(
        "CoverMeta",
        parent=normal_style,
        alignment=TA_CENTER,
        textColor=colors.HexColor("#4d473b"),
        spaceAfter=6,
    )

    doc = SimpleDocTemplate(
        str(output_path),
        pagesize=A4,
        leftMargin=42,
        rightMargin=42,
        topMargin=42,
        bottomMargin=42,
        title=title,
        author=author,
    )

    story = []

    generated_on = datetime.now().strftime("%d/%m/%Y")
    story.append(Spacer(1, 120))
    story.append(Paragraph("SENTIR FOTOGRÁFICO", cover_subtitle_style))
    story.append(Paragraph(escape(title), cover_title_style))
    story.append(Paragraph(escape(subtitle), cover_subtitle_style))
    story.append(Spacer(1, 28))
    story.append(Paragraph(f"<b>Documento:</b> {escape(title)}", cover_meta_style))
    story.append(Paragraph(f"<b>Autor:</b> {escape(author)}", cover_meta_style))
    story.append(Paragraph(f"<b>Fecha:</b> {generated_on}", cover_meta_style))
    story.append(Spacer(1, 30))
    story.append(Paragraph(
        "Este documento forma parte de la documentación institucional del sistema de gestión y publicación fotográfica.",
        cover_meta_style,
    ))
    story.append(PageBreak())

    for raw_line in md_text.splitlines():
        line = raw_line.rstrip()
        if not line.strip():
            story.append(Spacer(1, 6))
            continue

        if line.strip() == "---":
            story.append(Spacer(1, 8))
            continue

        if line.startswith("# "):
            story.append(Paragraph(escape(line[2:].strip()), title_style))
            continue

        if line.startswith("## "):
            story.append(Paragraph(escape(line[3:].strip()), h1_style))
            continue

        if line.startswith("### "):
            story.append(Paragraph(escape(line[4:].strip()), h2_style))
            continue

        bullet_match = re.match(r"^[-*]\s+(.+)$", line)
        if bullet_match:
            text = escape(bullet_match.group(1).strip())
            story.append(Paragraph(text, bullet_style, bulletText="•"))
            continue

        number_match = re.match(r"^\d+\.\s+(.+)$", line)
        if number_match:
            text = escape(line.strip())
            story.append(Paragraph(text, normal_style))
            continue

        cleaned = escape(line)
        cleaned = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", cleaned)
        story.append(Paragraph(cleaned, normal_style))

    doc.build(
        story,
        onFirstPage=lambda canvas, doc: draw_page_chrome(canvas, doc, title, subtitle),
        onLaterPages=lambda canvas, doc: draw_page_chrome(canvas, doc, title, subtitle),
    )


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Genera un PDF formal desde un archivo Markdown simple."
    )
    parser.add_argument(
        "-i", "--input",
        default=str(DEFAULT_INPUT_MD),
        help="Ruta del archivo markdown fuente"
    )
    parser.add_argument(
        "-o", "--output",
        default=str(DEFAULT_OUTPUT_PDF),
        help="Ruta del PDF de salida"
    )
    parser.add_argument(
        "-t", "--title",
        default="Manual de Usuario — Sentir Fotográfico",
        help="Título embebido en metadatos del PDF"
    )
    parser.add_argument(
        "-a", "--author",
        default="Sentir Fotográfico",
        help="Autor embebido en metadatos del PDF"
    )
    parser.add_argument(
        "-s", "--subtitle",
        default="Manual institucional",
        help="Subtítulo institucional visible en portada y encabezado"
    )

    args = parser.parse_args()

    input_md = Path(args.input).resolve()
    output_pdf = Path(args.output).resolve()

    if not input_md.exists():
        print(f"No se encontró el archivo fuente: {input_md}")
        return 1

    text = input_md.read_text(encoding="utf-8")
    build_pdf(text, output_pdf, args.title, args.author, args.subtitle)
    print(f"PDF generado: {output_pdf}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
