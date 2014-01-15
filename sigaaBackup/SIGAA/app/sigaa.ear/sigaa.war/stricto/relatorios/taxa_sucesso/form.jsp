<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Relatório de Taxa de Sucesso</h2>

<a4j:keepAlive beanName="relatorioTaxaSucessoStricto"/>
<div class="descricaoOperacao">
	<b>Caro usuário,</b> 
	<br/><br/>
	<p>Este relatório tem como objetivo de emitir a Taxa de Sucesso do programa, para fins de avaliação da PPG, 
	lista o total de ingressantes e de defesas por programa e a média de sucesso.</p>
	<p>Na elaboração deste relatório são verificados o total de alunos defendidos no programa no ano de 
	referência de defesa e o total de alunos ingressantes no ano informado calculando a taxa de sucesso.</p>
</div>		
<h:form id="form">
<table class="formulario" style="width: 70%">
<caption> Informe os Critérios para a Emissão do Relatório </caption>
	<tbody>
	<tr>
		<th>
			<c:choose>
				<c:when test="${!acesso.ppg}">
					<b>Programa:</b> 
				</c:when>
				<c:otherwise>
					Programa: 
				</c:otherwise>
			</c:choose>
		</th>
		<td>
		<c:if test="${acesso.ppg}">
			<h:selectOneMenu id="programa" value="#{relatorioTaxaSucessoStricto.unidade.id}">
				<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
				<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
			</h:selectOneMenu>
		</c:if>
		<c:if test="${!acesso.ppg}">
			${relatorioTaxaSucessoStricto.programaStricto}
		</c:if>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Nível:</th>
		<td>
			<h:selectOneMenu id="nivel" value="#{relatorioTaxaSucessoStricto.nivel}">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{nivelEnsino.strictoCombo}"/>
			</h:selectOneMenu> 			
		</td>
	</tr>		
	<tr>
		<th class="obrigatorio">Ano de Referência do Ingresso:</th>
		<td>
			<h:inputText value="#{relatorioTaxaSucessoStricto.anoIngresso}" id="anoIngresso" title="Ano de Referência do Ingresso" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> 			
		</td>
	</tr>		
	<tr>
		<th class="obrigatorio">Ano de Referência da Defesa:</th>
		<td>
			<h:inputText value="#{relatorioTaxaSucessoStricto.anoDefesa}" id="anoDefesa" title="Ano de Referência da Defesa" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> 			
		</td>
	</tr>			
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton id="btEmitir" action="#{relatorioTaxaSucessoStricto.gerarRelatorio}" value="Gerar Relatório"/>		
			<h:commandButton id="btCancelar" action="#{relatorioTaxaSucessoStricto.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
<br />
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>