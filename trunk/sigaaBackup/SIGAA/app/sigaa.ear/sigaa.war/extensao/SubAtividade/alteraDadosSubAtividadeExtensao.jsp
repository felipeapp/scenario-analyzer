<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<style>
#abas-descricao .aba {
	padding: 10px;
}

p.ajuda {
	text-align: center;
	margin: 5px 90px;
	color: #083772;
	font-style: italic;
}
div#msgAddProcessos{
	background:#FFF none repeat scroll 0 0;
	border:1px solid #FFDFDF;
	color:#FF1111;
	line-height:1.2em;
	padding:10px;
	width:95%;
}
</style>

<f:view>
<h2><ufrn:subSistema /> > Alterar Informações da SubAtividade</h2>

<div class="descricaoOperacao">
	<p>Caro Usuário,</p>
	<p>Utilize essa tela para alterar os dados de uma Mini Atividade.</p>
	<br/>
	<p>Uma Mini Atividade pode ser alterada a qualquer momento, independente da ação já ter sido aprovada ou já estar em execução.</p>
</div>

	<%-- Fica guardando os itens gerados entre requisicoes  --%>
	<a4j:keepAlive beanName="subAtividadeExtensaoMBean" />
	
	<%-- Para quando é chamada a partir da tela que lista as atividades do usuário --%>
	<a4j:keepAlive beanName="atividadeExtensao" /> 

	<h:form id="formAlteraDadosSubAtividade">
	
		<table class=formulario width="95%">
			
			<caption class="listagem">Informe os dados da subAtividade</caption>
			
			<tr>
				<td colspan="2" style="text-align: center; font-weight: bold;" > Atividade de Extensão: <h:outputText value="#{subAtividadeExtensaoMBean.obj.atividade.codigo}"/> - <h:outputText value="#{subAtividadeExtensaoMBean.obj.atividade.titulo}"/> </td>
			</tr>
			
			<tr>
				<th  class="required">Título:</th>
				<td><h:inputText value="#{subAtividadeExtensaoMBean.obj.titulo}" style="width: 95%;" id="tituloSubAtividade"/></td>
			</tr>
			
			<tr>
				<th class="required"> Tipo do Curso:</th>
				<td>
					<h:selectOneMenu id="tipoCurso"
						value="#{subAtividadeExtensaoMBean.obj.tipoSubAtividadeExtensao.id}" style="width: 70%;">
						<f:selectItem itemValue="-1" itemLabel="-- SELECIONE O TIPO DA MINI ATIVIDADE --"/>
						<f:selectItems value="#{cursoEventoExtensao.allTiposSubAtividade}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th  class="required">Local:</th>
				<td><h:inputText value="#{subAtividadeExtensaoMBean.obj.local}" style="width: 35%;" id="localSubAtividade"/></td>
			</tr>
			
			<tr>
			<th  class="required"> Período: </th>
			<td>
							
				<t:inputCalendar id="dataInicio" value="#{subAtividadeExtensaoMBean.obj.inicio}" 
					renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
					size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
					<f:converter converterId="convertData" />
				</t:inputCalendar>
				a <t:inputCalendar  id="dataFim" value="#{subAtividadeExtensaoMBean.obj.fim}" 
					renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
					size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
					<f:converter converterId="convertData" />
				</t:inputCalendar>				
				
			</td>
			
			<tr>
				<th  class="required">Horário:</th>
				<td><h:inputText value="#{subAtividadeExtensaoMBean.obj.horario}" style="width: 35%;" id="horarioSubAtividade"/></td>
			</tr>
			
			<tr>
				<th width="35%"  class="required"> Carga Horária:</th>
				<td >
					<h:inputText id="cargaHoraria" value="#{subAtividadeExtensaoMBean.obj.cargaHoraria}" size="4" maxlength="4"
						 style="text-align: right" onkeyup="formatarInteiro(this)" />
					horas
				</td>
			</tr>
			
			<tr>
				<th width="35%"  class="required"> Vagas:</th>
				<td >
					<h:inputText id="vagasSubAtividade" value="#{subAtividadeExtensaoMBean.obj.numeroVagas}" size="4" maxlength="4"
						 style="text-align: right" onkeyup="formatarInteiro(this)" />				
				</td>
			</tr>
			
			<tr>
			<td colspan="4" class="subFormulario">Outras Informações</td>
			</tr>
			<tr>
				<td colspan="6">
				<div id="abas-descricao">
					<div id="descricao-resumo" class="aba">
						<p class="ajuda">
							Utilize o espaço abaixo para colocar a descrição.
						</p>
						<h:inputTextarea id="resumo" value="#{subAtividadeExtensaoMBean.obj.descricao}" rows="8" style="width:97%;"/>
					</div>			
				</div>
				</td>
			</tr>
			
			<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton id="cmdButtonAtualizarSubAtividade" value="Atualizar" action="#{subAtividadeExtensaoMBean.atualizar}"/>
					<h:commandButton id="cmdButtonCancelar" value="<< Voltar" action="#{subAtividadeExtensaoMBean.cancelar}" immediate="true" onclick="#{confirm}"/>
				</td>
			</tr>
			</tfoot>
		</table>
		
	</h:form>

<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

</f:view>


<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script>
	

	tinyMCE.init({
		mode : "textareas", theme : "advanced", width : "100%", height : "250", language : "pt",
		theme_advanced_buttons1 : "cut,copy,paste,separator,search,replace,separator,bold,italic,underline,separator,strikethrough,justifyleft,justifycenter,justifyright,justifyfull,separator,bullist,numlist,image",
		theme_advanced_buttons2 : "fontselect,fontsizeselect,separator,undo,redo,separator,forecolor,backcolor,link,separator,sub,sup,charmap",
		theme_advanced_buttons3 : "",
		plugins : "searchreplace,contextmenu,advimage",
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left"
	});
	
	var Abas = {
		    init : function(){
		        var abas = new YAHOO.ext.TabPanel('abas-descricao');
		        abas.addTab('descricao-resumo', "Descrição");		        
		        abas.activate('descricao-resumo');
		    }
		};
		YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);

</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>