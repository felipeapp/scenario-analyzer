<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2 class="tituloPagina"><ufrn:subSistema /> > Publicar Resultado das Avaliações</h2>

<h:form id="escolher">

<div class="descricaoOperacao">
	 	Essa Operação publica o resultado das avaliaçôes dos projetos de ensino que concorreram a um edital. Escolha o edital para publicar o resultado. <br /> 
		* Caso existam projetos avaliados com discrepância de notas, haverá um redirecionamento para a página de visualização e distribuição de avaliadores desses projetos. <br />
		* Caso não existam projetos com discrepância de notas, haverá um redirecionamento para a página de publicação do resultado das avaliações.<br />
</div>
<br />
<table class="formulario" width="40%">
<caption>Editais</caption>

<tbody>
<tr>
	<th class="obrigatorio" ><h:outputLabel for="edital">Edital:</h:outputLabel></th>
	<td>    	
		<h:selectOneMenu id="edital" value="#{ publicarResultado.edital.id }">
			<f:selectItems value="#{ editalMonitoria.allCombo}"/>
		</h:selectOneMenu>
	</td>
</tr>
</tbody>

<tfoot>
<tr>
	<td colspan="2">
 	<h:commandButton action="#{ publicarResultado.iniciaBuscaAvaliacoes }" value="Buscar"/>
	<h:commandButton action="#{ publicarResultado.cancelar }" onclick="#{confirm}" value="Cancelar"/>
	</td>
</tr>
</tfoot>


</table>
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>