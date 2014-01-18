<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
		<a4j:keepAlive beanName="interrupcaoBibliotecaMBean"></a4j:keepAlive>

		<h2><ufrn:subSistema /> > Cadastrar Interrup��o para as Bibliotecas</h2>
		<br>
		<h:form id="fromCadastraInterrupcao">
			<h:inputHidden value="#{interrupcaoBibliotecaMBean.obj.id}"/>
	
		<div class="descricaoOperacao" style="width:90%">
			<p>Para cadastrar uma nova interrup��o para as bibliotecas, preencha o formul�rio abaixo.</p>
			<p>Todos os empr�stimos das bibliotecas selecionadas cujos prazos coincidirem com o per�odo escolhido, ser�o prorrogados para o pr�ximo dia �til.</p>
			<br/>
			<p><strong>IMPORTANTE:</strong> As prorroga��es dos empr�stimos n�o podem ser realizadas ao mesmo tempo em que os empr�stimos e as renova��es estiverem sendo feitas. 
			Por isso as opera��o de circula��o ser�o desativadas automaticamente durante o cadastro das interrup��es, voltando a ser ativadas logo ap�s o termino dessa opera��o.
			</p>
			<br/>
			<p>Observa��o: Ser� enviado um e-mail de car�ter informativo, com o novo prazo e o motivo da prorroga��o, a todos os usu�rios que possu�rem empr�stimos vencendo na data da interrup��o cadastrada.</p>
		</div>
	
		<table class="formulario" width="70%">
			<caption>Cadastrar Interrup��o</caption>
	
				<tr>
					<th class="obrigatorio">Motivo:</th>
					<td>
					<h:inputTextarea id="inpTextMotivoInterrupcao" value="#{interrupcaoBibliotecaMBean.obj.motivo}" cols="80" rows="4" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);"/>	
					</td>
				</tr>
				<tr>
					<th>Caracteres Restantes:</th>
					<td>
						<span id="quantidadeCaracteresDigitados">200</span>/200
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Data Inicial:</th>
					<td><t:inputCalendar value="#{interrupcaoBibliotecaMBean.dataInicio}" 
							id="dataInicio"
							size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
							renderPopupButtonAsImage="true"
							onkeypress="return formataData(this, event)" />
							
							Data Final:
							<t:inputCalendar value="#{interrupcaoBibliotecaMBean.dataFim}" 
							id="dataFim"
							size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
							renderPopupButtonAsImage="true"
							onkeypress="return formataData(this, event)" /><ufrn:help>Deixe vazio para cadastrar uma interrup��o de somente um dia.</ufrn:help>
							
							</td>
				</tr>
				
				<c:if test="${fn:length(interrupcaoBibliotecaMBean.bibliotecasPermissaoCadastroInterrupcao) > 0}">
					<tr>
						<td colspan="2">
							<table class="subFormulario" style="width:100%;">
								<caption>Bibliotecas para as quais a interrup��o vai ser cadastrada</caption>
								<thead>
									<tr>
										<th style="text-align:center;"><input type="checkbox" onclick="selecionaTodos(this);"/></th>
										<th style="text-align:left;">Identificador</th>
										<th style="text-align:left;">Nome</th>
									</tr>
								</thead>
								
								<c:forEach var="b" items="#{interrupcaoBibliotecaMBean.bibliotecasPermissaoCadastroInterrupcao}" varStatus="status">
									<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
										<td style="text-align:center;"><h:selectBooleanCheckbox styleClass="selecionar" value="#{b.selecionada}"/></td>
										<td style="text-align:left;">${b.identificador}</td>
										<td style="text-align:left;">${b.descricao}</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				</c:if>
				
				<tfoot>
					<tr>
						<td colspan="2" align="center">
							<h:commandButton id="botaoCadastraInterrupcao"  value="Cadastrar Interrup��o" action="#{interrupcaoBibliotecaMBean.cadastrar}" 
							onclick="return ativaBotaoFalso();" />
							
							<%-- Botao falso que � mostrado ao usu�rio desabilitado, porque n�o d� para desabilitar o bot�o geral, sen�o a a��o n�o � executada  --%>
							<h:commandButton id="fakecmdButtonCasdatrarInterrupcao" value="Aguarde ..." style="display: none;" disabled="true" />
							<span id="indicatorAguardarCadastro"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
							
							<h:commandButton value="Cancelar" action="#{interrupcaoBibliotecaMBean.telaInterrupcoesCadastradas}" id="voltarPaginaInterrupcoesCadastradas" onclick="#{confirm}" immediate="true" />
						</td>
					</tr>
				</tfoot>
			</table>
			
			<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
			
		</h:form>
</f:view>

<script type="text/javascript">
function selecionaTodos(chk){
	$A(document.getElementsByClassName('selecionar')).each(function(e) {
		e.checked = chk.checked;
	});
}

function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}



function ativaBotaoFalso() {
	
	// infelizmente n�o tem como colocar acento aqui //
	confirmou =  confirm('Confirma a interrupcao? Todos os emprestimos das bibliotecas selecionadas com prazo para a data desta interrupcao serao adiados para o proximo dia util?');
	
	if(confirmou){
		$('fromCadastraInterrupcao:botaoCadastraInterrupcao').hide();
		$('fromCadastraInterrupcao:fakecmdButtonCasdatrarInterrupcao').show();
		$('indicatorAguardarCadastro').style.display = '';
	}
	return confirmou;
	
}

ativaBotaoVerdadeiro();

function ativaBotaoVerdadeiro() {
	$('fromCadastraInterrupcao:botaoCadastraInterrupcao').show();
	$('fromCadastraInterrupcao:fakecmdButtonCasdatrarInterrupcao').hide();
	$('indicatorAguardarCadastro').style.display = 'none';
}

</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>