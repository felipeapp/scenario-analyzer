<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>

<script type="text/javascript">
tinyMCE.init({
	mode : "textareas", theme : "advanced", width : "100%", height : "250", language : "pt", 
	theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist",
	theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
	theme_advanced_buttons3 : "",
	plugins : "searchreplace,contextmenu,advimage",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "center"
});
</script>

<% CheckRoleUtil.checkRole(request, response, new int[] { SigaaPapeis.SEDIS, SigaaPapeis.COORDENADOR_PEDAGOGICO_EAD }); %>

<f:view>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<h2><ufrn:subSistema /> > Metodologia de Avaliação </h2>
	<br>
	<h:form id="form">

		<table class="formulario" width="90%">
			<caption> Cadastrar Metodologia de Avaliação </caption>
		
			<tr>
				<th>Curso:</th>
				<td>
					<strong><h:outputText value="#{metodologiaAvaliacaoEad.obj.curso.nome}"/></strong>				
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Método de Avaliação:</th>
				<td>
					<h:selectOneMenu value="#{ metodologiaAvaliacaoEad.obj.metodoAvaliacao }">
						<f:selectItems value="#{ metodologiaAvaliacaoEad.metodosAvaliacao }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th> Possui Tutor: </th>
				<td> 
					<h:selectBooleanCheckbox value="#{metodologiaAvaliacaoEad.obj.permiteTutor}">
						<a4j:support event="onclick" reRender="Peso_do_Tutor,Peso_do_Professor" actionListener="#{metodologiaAvaliacaoEad.calcularPorcentagemProfessor}"></a4j:support>
					</h:selectBooleanCheckbox>
				</td>
			</tr>				
			<tr>
				<th class="obrigatorio"> Peso do Tutor: </th>
				<td> 
					<h:inputText id="Peso_do_Tutor" value="#{metodologiaAvaliacaoEad.obj.porcentagemTutor}" size="10" readonly="#{metodologiaAvaliacaoEad.readOnly}" disabled="#{!metodologiaAvaliacaoEad.obj.permiteTutor}" >
						<a4j:support  event="onkeyup"  actionListener="#{metodologiaAvaliacaoEad.calcularPorcentagemProfessor}" reRender="Peso_do_Professor" oncomplete="return formatarInteiro(this);"/>
					</h:inputText> %
				</td>
			</tr>
			<tr>
				<th class="obrigatorio"> Peso do Professor: </th>
				<td> <h:inputText id="Peso_do_Professor" value="#{metodologiaAvaliacaoEad.obj.porcentagemProfessor}" size="10" readonly="#{metodologiaAvaliacaoEad.readOnly}" onkeyup="return formatarInteiro(this);" disabled="true"/> % <i>(Porcentagem calculada automaticamente)</i></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Ano-Período de início:</th>
				<td><h:inputText value="#{metodologiaAvaliacaoEad.obj.anoInicio}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>.<h:inputText value="#{metodologiaAvaliacaoEad.obj.periodoInicio}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="voltar" value="<< Voltar" action="#{metodologiaAvaliacaoEad.voltar}">
							<f:setPropertyActionListener target="#{metodologiaAvaliacaoEad.operacaoVoltar}" value="4" /> 
						</h:commandButton>
						<h:commandButton value="Cancelar" action="#{metodologiaAvaliacaoEad.cancelar}" onclick="#{confirm}" immediate="true"/>
						<h:commandButton id="proximo" value="Próximo >>" action="#{metodologiaAvaliacaoEad.submeterDadosGerais}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<br />
	<center><img src="/shared/img/required.gif" style="vertical-align: middle;" /> 
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
	<br />

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
