<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
<ufrn:subSistema></ufrn:subSistema> &gt;
Lista de Not�cias
</h2>

<html:form action="/ensino/latosensu/cadastroNoticiaLato">
	<table class="formulario" width="75%">
		<caption>Busca Not�cias</caption>

		<tbody>
			<tr>
				<td>
				<html:radio property="tipoBusca" value="1" styleClass="noborder"
					styleId="buscaTitulo" />
				</td>
				<td>T�tulo:</td>
            	<td>
            		<html:text property="obj.titulo" size="55" onfocus="javascript:forms[0].tipoBusca[0].checked=true;" />
            	</td>
			</tr>
			<tr>
				<td>
				<html:radio property="tipoBusca" value="2" styleClass="noborder"
					styleId="buscaData" />
				</td>
				<td>Data:</td>
            	<td>
            		<ufrn:calendar property="data" onchange="javascript:forms[0].tipoBusca[0].checked=true;" />
            	</td>
			</tr>
			<tr>
				<td>
				<html:radio property="tipoBusca" value="3" styleClass="noborder"
					styleId="buscaTodos" />
				</td>
				<td>Todos</td>
            </tr>
            </tbody>
            <tfoot>
            <tr>
				<td colspan="4" align="center">
				<html:hidden property="buscar" value="true"/>
				<html:button dispatch="list" value="Buscar"/>
				</td>
			</tr>
            </tfoot>
     </table>
</html:form>
<br>

<ufrn:table collection="${lista}"
	properties="titulo, data, publicada"
	headers="T�tulo, Data, Publicada"
	title="Not�cias" crud="true" />


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
