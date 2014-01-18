<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
		<a4j:keepAlive beanName="interrupcaoBibliotecaMBean"></a4j:keepAlive>

		<h2><ufrn:subSistema /> > Cadastrar Interrupção para as Bibliotecas</h2>
		<br>
		<h:form id="fromCadastraInterrupcao">
			<h:inputHidden value="#{interrupcaoBibliotecaMBean.obj.id}"/>
	
		<div class="descricaoOperacao" style="width:90%">
			<p>Para cadastrar uma nova interrupção para as bibliotecas, preencha o formulário abaixo.</p>
			<p>Todos os empréstimos das bibliotecas selecionadas cujos prazos coincidirem com o período escolhido, serão prorrogados para o próximo dia útil.</p>
			<br/>
			<p><strong>IMPORTANTE:</strong> As prorrogações dos empréstimos não podem ser realizadas ao mesmo tempo em que os empréstimos e as renovações estiverem sendo feitas. 
			Por isso as operação de circulação serão desativadas automaticamente durante o cadastro das interrupções, voltando a ser ativadas logo após o termino dessa operação.
			</p>
			<br/>
			<p>Observação: Será enviado um e-mail de caráter informativo, com o novo prazo e o motivo da prorrogação, a todos os usuários que possuírem empréstimos vencendo na data da interrupção cadastrada.</p>
		</div>
	
		<table class="formulario" width="70%">
			<caption>Cadastrar Interrupção</caption>
	
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
							onkeypress="return formataData(this, event)" /><ufrn:help>Deixe vazio para cadastrar uma interrupção de somente um dia.</ufrn:help>
							
							</td>
				</tr>
				
				<c:if test="${fn:length(interrupcaoBibliotecaMBean.bibliotecasPermissaoCadastroInterrupcao) > 0}">
					<tr>
						<td colspan="2">
							<table class="subFormulario" style="width:100%;">
								<caption>Bibliotecas para as quais a interrupção vai ser cadastrada</caption>
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
							<h:commandButton id="botaoCadastraInterrupcao"  value="Cadastrar Interrupção" action="#{interrupcaoBibliotecaMBean.cadastrar}" 
							onclick="return ativaBotaoFalso();" />
							
							<%-- Botao falso que é mostrado ao usuário desabilitado, porque não dá para desabilitar o botão geral, senão a ação não é executada  --%>
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
	
	// infelizmente não tem como colocar acento aqui //
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