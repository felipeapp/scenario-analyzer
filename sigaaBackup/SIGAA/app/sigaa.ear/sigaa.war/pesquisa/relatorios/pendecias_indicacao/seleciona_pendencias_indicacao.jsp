<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Relatório de Pendências de Indicação</h2>
	
	<div class="descricaoOperacao">
		<h3>Caro Gestor,</h3>
		<br/>
		<p>
			Este relatório traz uma listagem dos docentes que receberam cotas de bolsas de um edital 
			mas não indicaram alunos bolsistas para todas as cotas recebidas.
		</p>
	</div>
	
	<h:form id="formConsulta">
	<h:outputText value="#{relatorioPendenciasIndicacao.create}" />
		<table class="formulario" width="80%">
			<caption class="formulario">Dados para Emissão do Relatório</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Edital:</th>
					<td>
						<h:selectOneMenu id="editalPesquisa" value="#{relatorioPendenciasIndicacao.editalPesquisa.id}" style="width: 90%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatorioPendenciasIndicacao.allEditalPesquisaCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relatório" action="#{relatorioPendenciasIndicacao.gerarRelatorioPendenciasindicacao}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioPendenciasIndicacao.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>