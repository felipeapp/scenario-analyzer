<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/cadastroNoticiaLato.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt;
	Notícia
</h2>

    <table class="formulario" width="90%">

		<tbody>
			<tr>
				<td>
				<table class="subformulario">
				<caption>${noticia.titulo}</caption>
				<tr>
				<td>${noticia.texto}</td>
				</tr>
				</table>
                </td>
            </tr>
        </tbody>
	</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>