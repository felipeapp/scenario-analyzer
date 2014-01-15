<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Consultar Dados Quantitativos de Membros das Equipes de Extensão</h2>
	<a4j:keepAlive beanName="relatorioPlanejamentoMBean" />
	
	<h:messages showDetail="true" />
	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Consultar Relatório Gerais de Extensão</caption>
			<tbody>
				<h:inputHidden id="relatorioEscolhido"	value="#{relatorioPlanejamentoMBean.escolhaRelatorio}" />


				<tr>
					<th>Tipo de Relatório:</th>
					<td><b><h:outputText id="nomeRelatorioEscolhido"	value="#{relatorioPlanejamentoMBean.nomeRelatorioAtual}" /></b></td>
				</tr>

				<c:if test="${relatorioPlanejamentoMBean.escolhaRelatorio != 5 and relatorioPlanejamentoMBean.escolhaRelatorio != 6}">
					<tr>
						<th>Tipo da Ação:</th>
						<td>
						 	<h:selectOneMenu id="buscaTipoAcao" value="#{relatorioPlanejamentoMBean.buscaTipoAcao.id}">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
				    	 		<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
				    	 	</h:selectOneMenu>	    	 
						</td>
					</tr>
				</c:if>
				
 
 				<c:if test="${relatorioPlanejamentoMBean.escolhaRelatorio == 5 or relatorioPlanejamentoMBean.escolhaRelatorio == 6}">
					<tr>
						<th class="required">Área Temática:</th>
						<td>
							<h:selectOneMenu id="areaTematicaPrincipal"	
								value="#{relatorioPlanejamentoMBean.buscaAreaTematica.id}">
								<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
								<f:selectItems value="#{areaTematica.allCombo}"/>
							</h:selectOneMenu>
						</td>
					</tr>
 				</c:if>


				<tr>
			    	<th class="required"> <label for="situacaoAcao"> Situação da Ação: </label> </th>
			    	<td>
				    	 <h:selectOneMenu  id="buscaSituacao" value="#{relatorioPlanejamentoMBean.situacaoAcao.id}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				    	 	<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcaoExtensaoValidas}" />
			 			 </h:selectOneMenu>
			    	 
			    	</td>
			    </tr>


				<tr>
					<th class="required">Período de execução da ação:</th>
					<td>
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatorioPlanejamentoMBean.dataInicio}" id="dataInicio" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
						a
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" value="#{relatorioPlanejamentoMBean.dataFim}" id="dataFim" 
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"/>
					</td>
				</tr>
		
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Gerar Relatório" action="#{ relatorioPlanejamentoMBean.gerarRelatorio }"
							id="BotaoRelatorio" /> 
					<h:commandButton value="Cancelar" action="#{ relatorioPlanejamentoMBean.cancelar }"
							id="BotaoCancelar" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
		<div>
		<tr>
			<center><img src="/shared/img/required.gif" /> <span
				class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
		</tr>
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>