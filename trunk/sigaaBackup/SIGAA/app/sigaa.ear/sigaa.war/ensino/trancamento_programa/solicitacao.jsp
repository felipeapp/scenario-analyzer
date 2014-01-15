<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
</script>

<f:view>
	<f:subview id="menu">
		<%@include file="/portais/discente/menu_discente.jsp" %>
	</f:subview>
	
	<a4j:keepAlive beanName="trancamentoPrograma"/>

	<h2> <ufrn:subSistema /> &gt; Solicitação de Trancamento de Programa</h2>

	
	
	<c:set value="#{trancamentoPrograma.discente}" var="discente"></c:set>
	<%@include file="/graduacao/info_discente.jsp"%>	
	
	<h:form id="form">
		<table class="formulario" style="width: 70%;">
			<caption>Confirme o Trancamento</caption>		
			<tr>
				<th style="font-weight: bold; width: 200px">Ano-Período:</th>
				<td>${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo}</td>
			</tr>
			<c:if test="${trancamentoPrograma.obj.discente.stricto}">
				<tr>
					<th class="obrigatorio">Início do Trancamento:</th>
					<td><t:inputCalendar id="inicioTrancamento" value="#{trancamentoPrograma.obj.inicioTrancamento}" renderAsPopup="true"
								size="10" maxlength="10" onkeypress="formataData(this, event)" renderPopupButtonAsImage="true" /> 
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Número de meses:</th>
					<td>
						<h:inputText id="numeroMeses" value="#{trancamentoPrograma.obj.numeroMeses}" size="2" 
							maxlength="2" converter="#{ intConverter }" />
					</td>
				</tr>
			</c:if>			
			<tr>
				<td colspan="2">
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td align="center" colspan="2">
						<h:commandButton value="Confirmar Solicitação >>" action="#{trancamentoPrograma.confirmarSolicitacao}" rendered="#{!trancamentoPrograma.obj.posteriori}"/>
						<h:commandButton value="Confirmar Solicitação >>" action="#{trancamentoPrograma.confirmarSolicitacaoPosteriori}" rendered="#{trancamentoPrograma.obj.posteriori}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{trancamentoPrograma.cancelar}" />
					</td>
				</tr>
			</tfoot>			
		</table>	
		
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<br/>
	<table class="listagem" style="width:90%">
		<caption> Histórico de Solicitações </caption>
		<thead>
		<tr>
			<th> Matrícula </th>
			<th> Nome </th>
			<th> Curso </th>
			<th style="text-align: center;"> Ano-Período </th>
			<th> Status </th>
		</tr>
		</thead>
		<tbody>
		<c:if test="${empty trancamentoPrograma.solicitacoes}">
		<tr>
			<td colspan="5" style="text-align: center;">
				<i>Nenhuma Solicitação de Trancamento de Programa Cadastrada.</i>
			</td>
		</tr>
		</c:if>
		<c:if test="${not empty trancamentoPrograma.solicitacoes}">
			<c:forEach items="#{trancamentoPrograma.solicitacoes}" var="_solicitacao">
				<tr>
					<td> ${ _solicitacao.discente.matricula } </td>
					<td> ${ _solicitacao.discente.pessoa.nome } </td>
					<td> ${ _solicitacao.discente.curso.descricao } </td>
					<td style="text-align: center;"> ${ _solicitacao.ano }.${ _solicitacao.periodo } </td>
					<td> ${ _solicitacao.situacaoString } </td>
				</tr>
			</c:forEach>
		</c:if>
		</tbody>
	</table>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


<script type="text/javascript">
	J(document).ready(function(){
		ativaObs(J("#form input:radio:checked"),J("#observacao"));
					 
		J(".motivo").click(function(){
			ativaObs(J("#form input:radio:checked"),J("#observacao"));
		});		
	});

	function ativaObs(obj, obs){
		if (obj.val() == "5"){
			obs.css("display","inline-block");
		}else				
			obs.css("display","none");		
	}

	function informarOutroMotivo(obj){
		if (obj.value == '5') {
			$('outroMotivo').show();
		} else {
			$('outroMotivo').hide();
		}	
	}

	informarOutroMotivo(document.getElementById("form:motivoCombo"));
</script>
	