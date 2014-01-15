<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
	 
</style>

<f:view>
<a4j:keepAlive beanName="equivalenciaComponenteMBean"/>
<h2><ufrn:subSistema /> >  Ativar/Inativar Equivalências</h2>

<div class="descricaoOperacao">
<p>
Esta operação permite ativar ou desativar uma Equivalência para que ela
seja ou não considerada no histórico, que considera não só as equivalências
atuais mas também todas as que existiram durante o vínculo do aluno.
</p><br/>
<p>
* Caso a data não seja informada, será considerada a data atual para a desativação da expressão de equivalência.
</p>
<p>
* Utilize a operação <b>Desconsiderar a expressão de equivalência</b> para a expressão de equivalência não ser
utilizada nos cálculos dos discentes. Para reverter esta operação será necessário habilitar novamente a expressão com 
através do botão <b>Habilitar</b>.
</p>
</div>

<br/>
<div class="infoAltRem">

	<h:graphicImage alt="Habilitar" url="/img/check.png" style="overflow: visible;"/>: Habilitar
	<h:graphicImage alt="Desabilitar" url="/img/check_cinza.png" style="overflow: visible;"/>: Desabilitar
	<h:graphicImage alt="Desconsiderar Equivalência" url="/img/cross.png" style="overflow: visible;"/>: Desconsiderar a expressão de equivalência
</div>
<br/>
	
<h:form id="form">

	<table class="formulario" width="100%">
		<caption class="listagem">Equivalências </caption>

		<tbody>
			<tr><td>
			<rich:dataTable id="table" value="#{ equivalenciaComponenteMBean.listaDetalhes }" var="detalhe" 
				align="center" styleClass="formulario"  rowClasses="linhaPar, linhaImpar" rowKeyVar="det"
				columnClasses="colLeft,colCenter,colCenter,colCenter" headerClass="colCenter" width="100%">
			
				<rich:column>
					<f:facet name="header"><f:verbatim><p align="center"> Equivalência(s) do componente curricular ${equivalenciaComponenteMBean.obj.descricaoResumida}</p></f:verbatim></f:facet>
					<h:outputText value="#{ detalhe.equivalencia }"/>
				</rich:column>
			
				<rich:column>
					<f:facet name="header"><f:verbatim><p align="center">Início</p></f:verbatim></f:facet>
					<h:outputText value="#{detalhe.data}"/>
				</rich:column>
				
				<rich:column width="12%">
					<f:facet name="header"><f:verbatim><p align="center">Válida até</p></f:verbatim></f:facet>
					<t:inputCalendar value="#{detalhe.equivalenciaValidaAte}" renderAsPopup="true" size="10" maxlength="10" 
						id="dataFimEquiv" popupTodayString="Hoje:" renderPopupButtonAsImage="true" onkeypress="formataData(this,event)" 
						popupDateFormat="dd/MM/yyyy" disabled="#{ detalhe.equivalenciaValidaAte != null }">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</rich:column>
				
				<rich:column>
					<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
					
					<a4j:commandLink action="#{ equivalenciaComponenteMBean.habilitarEquivalencia }" reRender="form"
						rendered="#{ detalhe.equivalenciaValidaAte != null }" id="btaoHabilitarEquivalenvia" style="width: 80px" > 
						<f:param name="idDetalhe" value="#{detalhe.id}"/>
						<h:graphicImage alt="Habilitar" url="/img/check.png" title="Habilitar expressão de equivalência"/>
					</a4j:commandLink>
					
					<a4j:commandLink action="#{ equivalenciaComponenteMBean.desabilitarEquivalencia }" reRender="form"
						rendered="#{ detalhe.equivalenciaValidaAte == null }" id="btnDesabilitarEquiv" style="width: 80px" 
						onclick="confirm('Deseja realmente DESABILITAR esta expressão de equivalência?');">
						<h:graphicImage alt="Desabilitar" url="/img/check_cinza.png" title="Desabilitar expressão de equivalência" />
						<f:param name="idDetalhe" value="#{detalhe.id}"/>
					</a4j:commandLink>
					
					<a4j:commandLink action="#{ equivalenciaComponenteMBean.desconsiderarEquivalencia }" reRender="form"
						rendered="#{ !detalhe.desconsiderarEquivalencia }" id="btnDesconsiderarEquiv" style="width: 80px" 
						onclick="confirm('Deseja realmente DESCONSIDERAR esta expressão de equivalência?');">
						<h:graphicImage alt="Desconsiderar Equivalencia" url="/img/cross.png" title="Desconsiderar a expressão de equivalência" />
						<f:param name="idDetalhe" value="#{detalhe.id}"/>
					</a4j:commandLink>
				</rich:column>
				
			</rich:dataTable>
			</td></tr>
		</tbody>	
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: center;">
					<h:commandButton value="<< Voltar" action="#{equivalenciaComponenteMBean.formBusca}" id="botaoUtilizadoParaVoltar"/>				
				</td>				
			</tr>		
		</tfoot>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>