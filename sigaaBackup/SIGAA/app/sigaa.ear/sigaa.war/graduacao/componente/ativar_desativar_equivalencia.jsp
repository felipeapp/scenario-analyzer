<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
	 
</style>

<f:view>
<a4j:keepAlive beanName="equivalenciaComponenteMBean"/>
<h2><ufrn:subSistema /> >  Ativar/Inativar Equival�ncias</h2>

<div class="descricaoOperacao">
<p>
Esta opera��o permite ativar ou desativar uma Equival�ncia para que ela
seja ou n�o considerada no hist�rico, que considera n�o s� as equival�ncias
atuais mas tamb�m todas as que existiram durante o v�nculo do aluno.
</p><br/>
<p>
* Caso a data n�o seja informada, ser� considerada a data atual para a desativa��o da express�o de equival�ncia.
</p>
<p>
* Utilize a opera��o <b>Desconsiderar a express�o de equival�ncia</b> para a express�o de equival�ncia n�o ser
utilizada nos c�lculos dos discentes. Para reverter esta opera��o ser� necess�rio habilitar novamente a express�o com 
atrav�s do bot�o <b>Habilitar</b>.
</p>
</div>

<br/>
<div class="infoAltRem">

	<h:graphicImage alt="Habilitar" url="/img/check.png" style="overflow: visible;"/>: Habilitar
	<h:graphicImage alt="Desabilitar" url="/img/check_cinza.png" style="overflow: visible;"/>: Desabilitar
	<h:graphicImage alt="Desconsiderar Equival�ncia" url="/img/cross.png" style="overflow: visible;"/>: Desconsiderar a express�o de equival�ncia
</div>
<br/>
	
<h:form id="form">

	<table class="formulario" width="100%">
		<caption class="listagem">Equival�ncias </caption>

		<tbody>
			<tr><td>
			<rich:dataTable id="table" value="#{ equivalenciaComponenteMBean.listaDetalhes }" var="detalhe" 
				align="center" styleClass="formulario"  rowClasses="linhaPar, linhaImpar" rowKeyVar="det"
				columnClasses="colLeft,colCenter,colCenter,colCenter" headerClass="colCenter" width="100%">
			
				<rich:column>
					<f:facet name="header"><f:verbatim><p align="center"> Equival�ncia(s) do componente curricular ${equivalenciaComponenteMBean.obj.descricaoResumida}</p></f:verbatim></f:facet>
					<h:outputText value="#{ detalhe.equivalencia }"/>
				</rich:column>
			
				<rich:column>
					<f:facet name="header"><f:verbatim><p align="center">In�cio</p></f:verbatim></f:facet>
					<h:outputText value="#{detalhe.data}"/>
				</rich:column>
				
				<rich:column width="12%">
					<f:facet name="header"><f:verbatim><p align="center">V�lida at�</p></f:verbatim></f:facet>
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
						<h:graphicImage alt="Habilitar" url="/img/check.png" title="Habilitar express�o de equival�ncia"/>
					</a4j:commandLink>
					
					<a4j:commandLink action="#{ equivalenciaComponenteMBean.desabilitarEquivalencia }" reRender="form"
						rendered="#{ detalhe.equivalenciaValidaAte == null }" id="btnDesabilitarEquiv" style="width: 80px" 
						onclick="confirm('Deseja realmente DESABILITAR esta express�o de equival�ncia?');">
						<h:graphicImage alt="Desabilitar" url="/img/check_cinza.png" title="Desabilitar express�o de equival�ncia" />
						<f:param name="idDetalhe" value="#{detalhe.id}"/>
					</a4j:commandLink>
					
					<a4j:commandLink action="#{ equivalenciaComponenteMBean.desconsiderarEquivalencia }" reRender="form"
						rendered="#{ !detalhe.desconsiderarEquivalencia }" id="btnDesconsiderarEquiv" style="width: 80px" 
						onclick="confirm('Deseja realmente DESCONSIDERAR esta express�o de equival�ncia?');">
						<h:graphicImage alt="Desconsiderar Equivalencia" url="/img/cross.png" title="Desconsiderar a express�o de equival�ncia" />
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