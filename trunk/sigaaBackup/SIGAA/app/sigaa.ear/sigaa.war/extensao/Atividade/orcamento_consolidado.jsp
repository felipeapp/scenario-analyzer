<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript">
<!--

	function totalConsolidado(i){

				fundo = $('form:dt_'+ i +':'+ i + ':fundo');
				fundacao = $('form:dt_'+ i +':'+ i + ':fundacao');
				outros = $('form:dt_'+ i +':'+ i + ':outros');

				tc = $('form:dt_'+ i +':'+ i + ':tc');

				tc.value = fundo.value.toNumber() + fundacao.value.toNumber() + outros.value.toNumber();

	}

//-->
</script>


<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	
		<h2><ufrn:subSistema /> &gt; Consolidação do Orçamento</h2>
	
	<div class="descricaoOperacao">
		<table width="100%">
			<tr>
				<td width="50%">
				Nesta tela deve ser informado como devem ser divididas as despesas de uma Ação.
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

	<h:form id="form">
		<table class="listagem">
			<caption class="listagem">Consolidação do Orçamento Detalhado</caption>
			<thead>
				<tr>
					<td>
						<c:if test="${not empty atividadeExtensao.obj.orcamentosConsolidados}">
							<t:dataTable id="dt" value="#{atividadeExtensao.obj.orcamentosConsolidados}" var="consolidacao"
							 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" forceIdIndex="true">
								<t:column>
									<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
									<h:outputText value="#{consolidacao.elementoDespesa.descricao}" />
								</t:column>
	
								<t:column>
									<f:facet name="header"><f:verbatim>Interno (FAEx)</f:verbatim></f:facet>
									<f:verbatim rendered="#{atividadeExtensao.obj.financiamentoInterno}">R$ </f:verbatim>
									<h:inputText value="#{consolidacao.fundo}"  id="fundo" size="12" onkeypress="return(formataValor(this, event, 2))" title="Orçamento Interno (FAEX)" rendered="#{atividadeExtensao.obj.financiamentoInterno}">
										<f:converter converterId="convertMoeda"/>
									</h:inputText>
									<f:verbatim rendered="#{!atividadeExtensao.obj.financiamentoInterno}"><font color="red">Não Solicitado</font></f:verbatim>
								</t:column>
	
								<t:column headerstyle="text-align:right" style="text-align:right">
									<f:facet name="header"><f:verbatim>Fundação (Funpec)</f:verbatim></f:facet>
									<f:verbatim>R$ </f:verbatim>
									<h:inputText style="text-align:right;" value="#{consolidacao.fundacao}" id="fundacao" title="Fundação (Funpec)" size="20" maxlength="13" onkeypress="return(formataValor(this, event, 2))">
										<f:converter converterId="convertMoeda"/>
									</h:inputText>
								</t:column>
								
								<t:column headerstyle="text-align:right" style="text-align:right">
									<f:facet name="header"><f:verbatim>Outros (Externo)</f:verbatim></f:facet>
									<f:verbatim>R$ </f:verbatim>
									<h:inputText style="text-align:right" value="#{consolidacao.outros}" id="outros" label="Outros (Externo)" size="20" maxlength="13" onkeypress="return(formataValor(this, event, 2))">
										<f:converter converterId="convertMoeda"/>
									</h:inputText>
								</t:column>
	
								<t:column headerstyle="text-align:right" style="text-align:right">
									<f:facet name="header"><f:verbatim>Total Orçamento</f:verbatim></f:facet>
									<h:outputText value="#{consolidacao.totalOrcamento}">
										<f:convertNumber pattern="R$ #,##0.00"/>
									</h:outputText>
								</t:column>
							</t:dataTable>
						</c:if>
					</td>
				</tr>
			</thead>
						
			<c:if test="${empty atividadeExtensao.obj.orcamentosConsolidados}">
				<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
			</c:if>			
		</table>
	
	
		<table class="formulario" width="100%">
			<tfoot>
				<tr>
					<td colspan="2">
						<h:form>
							<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" id="voltar" immediate="true"/>
							<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}" id="cancelar"  onclick="#{confirm}" immediate="true"/>
							<h:commandButton value="Avançar >>" action="#{atividadeExtensao.submeterOrcamentoConsolidado}" id="avancar"/>
						</h:form>
					</td>
				</tr>
			</tfoot>
		</table>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>