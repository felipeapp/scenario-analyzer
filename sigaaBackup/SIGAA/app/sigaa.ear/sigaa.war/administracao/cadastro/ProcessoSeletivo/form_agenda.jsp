<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<style>
	.colSemana{width:100px;text-align: left; }
	.colData{width:150px;text-align: center; }
	.colQtd{width:150px;text-align: center; }
	.colIcone{width:25px;text-align: right;}
</style>

<f:view>
	<h2><ufrn:subSistema /> > Processo Seletivo > Cadastro do Período de Agendamento</h2>
	<h:messages ></h:messages>

	<center>
		<h:form>
		<div class="infoAltRem" style="text-align: center; width: 100%">
			<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
  			<h:commandLink value="Listar processos seletivos cadastrados" action="#{processoSeletivo.listar}"/>
		</div>
		</h:form>
	</center>
	<br clear="all"/>
	
	<h:form id="form" enctype="multipart/form-data">
	
		<table class="formulario" width="99%">
			<caption class="listagem">
				<h:outputText value="#{processoSeletivo.obj.editalProcessoSeletivo.nome}"/>
			</caption>
			<tbody>
				<tr>
					<th class="required">Período de Agendamento:</th>
					<td>
					<t:inputCalendar value="#{processoSeletivo.obj.editalProcessoSeletivo.inicioPeriodoAgenda}" size="10" disabled="#{processoSeletivo.readOnly}"
							id="inicioPeriodoAgenda" maxlength="10" onkeypress="return formataData(this,event);"
							 renderAsPopup="true" rendered="#{processoSeletivo.obj.id==0 || !processoSeletivo.obj.inscricoesAbertas}" 
							renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" title="Início do Período de Agendamento" />
					<h:outputText value="#{processoSeletivo.obj.editalProcessoSeletivo.inicioPeriodoAgenda}" rendered="#{processoSeletivo.obj.id>0 && processoSeletivo.obj.inscricoesAbertas}" />			
					a
					<t:inputCalendar value="#{processoSeletivo.obj.editalProcessoSeletivo.fimPeriodoAgenda}" size="10"  disabled="#{processoSeletivo.readOnly}"
							id="fimPeriodoAgenda" maxlength="10" onkeypress="return formataData(this,event);"
							 renderAsPopup="true"  rendered="#{processoSeletivo.obj.id==0 || !processoSeletivo.obj.inscricoesAbertas}" 
							renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy"  title="Fim do Período de Agendamento" />
					
					<h:outputText value="#{processoSeletivo.obj.editalProcessoSeletivo.fimPeriodoAgenda}" rendered="#{processoSeletivo.obj.id>0 && processoSeletivo.obj.inscricoesAbertas}" />			
					
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Definir Período" action="#{processoSeletivo.adicionarAgendas}" id="periodo" 
						rendered="#{processoSeletivo.obj.id==0 || (!processoSeletivo.obj.inscricoesAbertas)}"   />
						&nbsp;
					</td>
				</tr>
			</tfoot>	
		</table>
		
		<br/>

		<table class="formulario" width="85%">
			<thead>
				<tr>
					<td class="colSemana" >
					Dias da Semana
					</td>
					<td class="colData" >
					Data de Agendamento
					</td>
					<td class="colQtd">
					Qtd Máx. de Inscritos
					</td>

				</tr>
			</thead>
		</table>	
		<center>		
		<t:dataTable id="dtTblAgenda"  value="#{processoSeletivo.obj.editalProcessoSeletivo.agendas}" var="campoControle" rowIndexVar="linha" 
		 columnClasses="colSemana,colData,colQtd,colIcone" width="85%">
		 	<h:column>
				&nbsp;
				<h:outputText value="#{campoControle.diaSemana}"/>
			</h:column>
			<h:column>
				<h:outputText value="#{campoControle.dataAgenda}"/>
			</h:column>
			<h:column>
				<h:inputText size="5" maxlength="5" readonly="#{processoSeletivo.obj.id>0 && processoSeletivo.obj.inscricoesAbertas}" value="#{campoControle.qtd}"/>
			</h:column>
		</t:dataTable>
		</center>  	
		<br>	
		<table class="formulario" width="85%">
			<tfoot>
				<tr>
					<td align="center">
						<h:commandButton immediate="true" value="<< Dados do Processo Seletivo" id="processoSeletivo"
						action="#{processoSeletivo.formDadosProcessoSeletivo}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar"
						action="#{processoSeletivo.cancelar}" />
						<h:commandButton value="Próximo Passo >>" id="proxPasso"
						action="#{processoSeletivo.formCursosProcessoSeletivo}" />
					</td>
				</tr>
			</tfoot>
		</table>
	
	</h:form>
	
	<br />
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/>
	   <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>