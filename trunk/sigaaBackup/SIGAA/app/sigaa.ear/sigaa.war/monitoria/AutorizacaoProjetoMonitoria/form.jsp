<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Autorização do Projeto de Monitoria</h2>
	
	<h:form id="relatorio">

		<table class="visualizacao" width="80%">
			<caption class="listagem">Análise do Projeto de Ensino</caption>

			<tr>
				<td colspan="3" class="subFormulario">
					Detalhes do Projeto de Ensino
				</td>
			</tr>
			
			<tr>
				<th width="20%">Ano - Título:</th>
				<td><h:outputText value="#{autorizacaoProjetoMonitoria.obj.projetoEnsino.anoTitulo}"/></td>
			</tr>

			<tr>
				<th width="20%">Coordenador(a):</th>
				<td><h:outputText value="#{autorizacaoProjetoMonitoria.obj.projetoEnsino.coordenacao.pessoa.nome}"/></td>
			</tr>

			<tr>
				<th width="20%" valign="top">Resumo:</th>
				<td align="justify"><h:outputText value="#{autorizacaoProjetoMonitoria.obj.projetoEnsino.resumo}" escape="false"/></td>
			</tr>
							
			<tr>
				<td class="subFormulario" colspan="3">Parecer</td>
			</tr>
			
			<tr>
				<td width="35%" align="right">Autorizar Projeto de Ensino:</th>
				<td>						
					<h:selectOneRadio id="radioAutorizado"
						value="#{autorizacaoProjetoMonitoria.obj.autorizado}" >
						<f:selectItem itemLabel="Sim" itemValue="true" />
						<f:selectItem itemLabel="Não" itemValue="false" />
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<td align="right">Tipo de Autorização:<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/></td>
				<td>
					<h:selectOneMenu value="#{autorizacaoProjetoMonitoria.obj.tipoAutorizacao.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{autorizacaoProjetoMonitoria.tiposAutorizacoesCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td align="right">Data da Reunião:<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/></td>
				<td>
					<t:inputCalendar  popupDateFormat="dd/MM/yyyy" 
						id="txtDataReuniao" value="#{autorizacaoProjetoMonitoria.obj.dataReuniao}" 
						renderAsPopup="true" 
						renderPopupButtonAsImage="true"
						popupTodayString="Hoje é"
						size="10" onkeypress="return(formataData(this,event))"  maxlength="10"/>
				</td>
			</tr>			
			
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Confirmar" action="#{autorizacaoProjetoMonitoria.autorizar}" /> 
						<h:commandButton value="Cancelar" action="#{autorizacaoProjetoMonitoria.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	
	<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
	
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>