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
<h2><ufrn:subSistema /> > Informações da SubAtividade</h2>

<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			Nesta tela devem ser informados os dados gerais de uma Ação.
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<p><strong>OBSERVAÇÃO:</strong> Os dados informados só são cadastrados na base de dados quando clica-se em "Avançar >>".</p> 
			</td>
		</tr>
	</table>
</div>

<h:messages showDetail="true"></h:messages>

<h:form id="formCursoEvento">

	<table class=formulario width="95%">
		<caption class="listagem">Informe os dados da subAtividade</caption>
		
		<tr>
			<th  class="required">Título:</th>
			<td><h:inputText value="#{cursoEventoExtensao.subAtividade.titulo}" style="width: 95%;" id="tituloSubAtividade"/></td>
		</tr>
		
		<tr>
			<th class="required"> Tipo do Curso:</th>
			<td>
				<h:selectOneMenu id="tipoCurso"
					value="#{cursoEventoExtensao.subAtividade.tipoSubAtividadeExtensao.id}" style="width: 70%;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE O TIPO DA MINI ATIVIDADE --"/>
					<f:selectItems value="#{cursoEventoExtensao.allTiposSubAtividade}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<th  class="required">Local:</th>
			<td><h:inputText value="#{cursoEventoExtensao.subAtividade.local}" style="width: 35%;" id="localSubAtividade"/></td>
		</tr>
		
		<tr>
		<th  class="required"> Período: </th>
		<td>
						
			<t:inputCalendar id="dataInicio" value="#{cursoEventoExtensao.subAtividade.inicio}" 
				renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
				size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
				<f:converter converterId="convertData" />
			</t:inputCalendar>
			a <t:inputCalendar  id="dataFim" value="#{cursoEventoExtensao.subAtividade.fim}" 
				renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
				size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
				<f:converter converterId="convertData" />
			</t:inputCalendar>				
			
		</td>
		
		<tr>
			<th  class="required">Horário:</th>
			<td><h:inputText value="#{cursoEventoExtensao.subAtividade.horario}" style="width: 35%;" id="horarioSubAtividade"/></td>
		</tr>
		
		<tr>
			<th width="35%"  class="required"> Carga Horária:</th>
			<td >
				<h:inputText id="cargaHoraria" value="#{cursoEventoExtensao.subAtividade.cargaHoraria}" size="4" maxlength="4"
					 style="text-align: right" onkeyup="formatarInteiro(this)" />
				horas
			</td>
		</tr>
		
		<tr>
			<th width="35%"  class="required"> Vagas:</th>
			<td >
				<h:inputText id="vagasSubAtividade" value="#{cursoEventoExtensao.subAtividade.numeroVagas}" size="4" maxlength="4"
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
					Utilize o espaço abaixo para colocar a descrição. <h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
				</p>
				<h:inputTextarea id="resumo" value="#{cursoEventoExtensao.subAtividade.descricao}" rows="8" style="width:97%;"/>
			</div>			
		</div>
		</td>
	</tr>
	
	
	<tr style="background: #DEDFE3;">
		<td colspan="2" align="center">
			<h:commandButton action="#{cursoEventoExtensao.adicionarSubAtividade}" value="Adicionar Mini Atividade" />
		</td>
	</tr> 
	<tr><td><br/></td></tr>
	<tr><td><br/></td></tr>
	<tr><td><br/></td></tr>		
	
	<c:if test="${not empty cursoEventoExtensao.subAtividadesAtivas}">
					<tr>
						<td colspan="2">
						<h2 style="text-align: center; background: #EFF3FA; font-size: 12px">Mini Atividades Inseridas</h2>
				
									<t:dataTable value="#{cursoEventoExtensao.subAtividadesAtivas}" rowIndexVar="row" var="subAtv" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbEquipe">
										
																				
												<t:column>
													<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.titulo}" />
												</t:column>												
												
												<t:column>
													<f:facet name="header"><f:verbatim>Tipo Mini Atividade</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.tipoSubAtividadeExtensao.descricao}" />
												</t:column>
												
												<t:column>
													<f:facet name="header"><f:verbatim>Local</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.local}" />
												</t:column>
												
												<t:column>
													<f:facet name="header"><f:verbatim>Início</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.inicio}" />
												</t:column>
												
												<t:column>
													<f:facet name="header"><f:verbatim>Fim</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.fim}" />
												</t:column>
												
												<t:column>
													<f:facet name="header"><f:verbatim>Horário</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.horario}" />
												</t:column>
												
												<t:column>
													<f:facet name="header"><f:verbatim>Carga Horária</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.cargaHoraria}" />
												</t:column>
												
												<t:column>
													<f:facet name="header"><f:verbatim>Vagas</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.numeroVagas}" />
												</t:column>
												
												<t:column>
													<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
													<h:outputText value="#{subAtv.descricao}" escape="false"/>
												</t:column>
												
												
												<t:column>													
													<h:commandLink title="Remover Mini Atividade" action="#{ cursoEventoExtensao.removerSubAtividade }">
									      				<f:param name="tituloSubAtividade" value="#{row}"/>
									      				<h:graphicImage url="/img/delete.gif" />
													</h:commandLink>																					
												</t:column>
												
											
									</t:dataTable>
									
									<br/>
									<br/>
									<br/>
									
									
						</td> 
					</tr>
	</c:if>		
		
		
		
		
		
	
	
	
	
		<tfoot>
		<tr>
			<td colspan="4">
				<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" id="btVoltar"/>
				<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" id="btCancelar" onclick="#{confirm}"/>
				<h:commandButton value="Avançar >>" action="#{cursoEventoExtensao.submeterSubAtividades}" id="btAvancar"/>
			</td>
		</tr>
		</tfoot>
	</table>
</h:form>
	<br />
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br />
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