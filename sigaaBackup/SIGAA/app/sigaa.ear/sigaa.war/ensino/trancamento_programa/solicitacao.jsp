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

	<h2> <ufrn:subSistema /> &gt; Solicita��o de Trancamento de Programa</h2>

	
	
	<c:set value="#{trancamentoPrograma.discente}" var="discente"></c:set>
	<%@include file="/graduacao/info_discente.jsp"%>	
	
	<h:form id="form">
		<table class="formulario" style="width: 70%;">
			<caption>Confirme o Trancamento</caption>		
			<tr>
				<th style="font-weight: bold; width: 200px">Ano-Per�odo:</th>
				<td>${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo}</td>
			</tr>
			<c:if test="${trancamentoPrograma.obj.discente.stricto}">
				<tr>
					<th class="obrigatorio">In�cio do Trancamento:</th>
					<td><t:inputCalendar id="inicioTrancamento" value="#{trancamentoPrograma.obj.inicioTrancamento}" renderAsPopup="true"
								size="10" maxlength="10" onkeypress="formataData(this, event)" renderPopupButtonAsImage="true" /> 
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">N�mero de meses:</th>
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
						<h:commandButton value="Confirmar Solicita��o >>" action="#{trancamentoPrograma.confirmarSolicitacao}" rendered="#{!trancamentoPrograma.obj.posteriori}"/>
						<h:commandButton value="Confirmar Solicita��o >>" action="#{trancamentoPrograma.confirmarSolicitacaoPosteriori}" rendered="#{trancamentoPrograma.obj.posteriori}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{trancamentoPrograma.cancelar}" />
					</td>
				</tr>
			</tfoot>			
		</table>	
		
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	<br/>
	<table class="listagem" style="width:90%">
		<caption> Hist�rico de Solicita��es </caption>
		<thead>
		<tr>
			<th> Matr�cula </th>
			<th> Nome </th>
			<th> Curso </th>
			<th style="text-align: center;"> Ano-Per�odo </th>
			<th> Status </th>
		</tr>
		</thead>
		<tbody>
		<c:if test="${empty trancamentoPrograma.solicitacoes}">
		<tr>
			<td colspan="5" style="text-align: center;">
				<i>Nenhuma Solicita��o de Trancamento de Programa Cadastrada.</i>
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
	