<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2 class="tituloPagina"><ufrn:subSistema /> > Publicar Resultado das Avalia��es</h2>

<h:form id="escolher">

<div class="descricaoOperacao">
	 	Essa Opera��o publica o resultado das avalia��es dos projetos de ensino que concorreram a um edital. Escolha o edital para publicar o resultado. <br /> 
		* Caso existam projetos avaliados com discrep�ncia de notas, haver� um redirecionamento para a p�gina de visualiza��o e distribui��o de avaliadores desses projetos. <br />
		* Caso n�o existam projetos com discrep�ncia de notas, haver� um redirecionamento para a p�gina de publica��o do resultado das avalia��es.<br />
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
<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> </center><br/>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>