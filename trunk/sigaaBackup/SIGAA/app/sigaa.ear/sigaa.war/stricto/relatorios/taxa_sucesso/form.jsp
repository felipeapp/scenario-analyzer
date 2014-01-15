<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Relat�rio de Taxa de Sucesso</h2>

<a4j:keepAlive beanName="relatorioTaxaSucessoStricto"/>
<div class="descricaoOperacao">
	<b>Caro usu�rio,</b> 
	<br/><br/>
	<p>Este relat�rio tem como objetivo de emitir a Taxa de Sucesso do programa, para fins de avalia��o da PPG, 
	lista o total de ingressantes e de defesas por programa e a m�dia de sucesso.</p>
	<p>Na elabora��o deste relat�rio s�o verificados o total de alunos defendidos no programa no ano de 
	refer�ncia de defesa e o total de alunos ingressantes no ano informado calculando a taxa de sucesso.</p>
</div>		
<h:form id="form">
<table class="formulario" style="width: 70%">
<caption> Informe os Crit�rios para a Emiss�o do Relat�rio </caption>
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
		<th class="obrigatorio">N�vel:</th>
		<td>
			<h:selectOneMenu id="nivel" value="#{relatorioTaxaSucessoStricto.nivel}">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{nivelEnsino.strictoCombo}"/>
			</h:selectOneMenu> 			
		</td>
	</tr>		
	<tr>
		<th class="obrigatorio">Ano de Refer�ncia do Ingresso:</th>
		<td>
			<h:inputText value="#{relatorioTaxaSucessoStricto.anoIngresso}" id="anoIngresso" title="Ano de Refer�ncia do Ingresso" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> 			
		</td>
	</tr>		
	<tr>
		<th class="obrigatorio">Ano de Refer�ncia da Defesa:</th>
		<td>
			<h:inputText value="#{relatorioTaxaSucessoStricto.anoDefesa}" id="anoDefesa" title="Ano de Refer�ncia da Defesa" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> 			
		</td>
	</tr>			
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton id="btEmitir" action="#{relatorioTaxaSucessoStricto.gerarRelatorio}" value="Gerar Relat�rio"/>		
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