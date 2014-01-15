<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
		<h2> <ufrn:subSistema /> &gt; Criar Suspens�es Manuais para os Usu�rio da Biblioteca</h2>
		
		<a4j:keepAlive beanName="suspensaoUsuarioBibliotecaMBean" />
		<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
		
		<div class="descricaoOperacao">
			<p>Utilize esta p�gina para fazer um cadastro manual de suspens�o para um usu�rio.</p>
		</div>

		<%-- Exibe as informa��es do usu�rio --%>
		<c:set var="_infoUsuarioCirculacao" value="${suspensaoUsuarioBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
	
		<table class="formulario" style="width:80%;">
			<caption> Nova Suspens�o</caption>

			<tbody>
				<tr>
					<td colspan="4" style="color: red; font-weight: bold; text-align: center;">
						<t:div id="divMensagemAlertaSuspensao" rendered="#{suspensaoUsuarioBibliotecaMBean.usuarioEstaSuspenso}">
							O usuario est� suspenso at� o dia <ufrn:format type="data" valor="${suspensaoUsuarioBibliotecaMBean.obj.dataInicio}" />
						</t:div>
					</td>
				</tr>
			  
				<tr>
					<td colspan="4">
						<t:div id="divCalculadora">
						<table id="tabelaCalculadora" class="subFormulario" style="width: 80%; margin-bottom: 20px; border: 1px solid #DEDFE3;">
							<caption> Calculadora dos prazos das Suspens�es</caption>
							
							<tr>
								<td colspan="2">
									<div class="descricaoOperacao">
										<p>O c�lculo leva em considera��o que todos os empr�stimos possuem o mesmo tipo de prazo.</p>
									</div>
								</td>
							</tr>
							
							<tr>
								<th width="40%">Prazo para Devolu��o:</th>
								<td> 
									<t:inputCalendar value="#{suspensaoUsuarioBibliotecaMBean.prazoDevolucaoCalculoManual}" 
										size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
										renderPopupButtonAsImage="true"
										onkeypress="return formataData(this, event)" />
								</td>
							</tr>
							<tr>
								<th>Data da Devolu��o:</th>
								<td> 
									<t:inputCalendar value="#{suspensaoUsuarioBibliotecaMBean.dataDevolucaoCalculoManual}" 
										size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
										renderPopupButtonAsImage="true"
										onkeypress="return formataData(this, event)" />
								</td>
							</tr>
							<tr>
								<th>Tipo do Prazo:</th>
								<td> 
									<h:selectOneMenu id="tipoPrazoEmprestimos" value="#{suspensaoUsuarioBibliotecaMBean.tipoPrazoCalculoManual}">
										<f:selectItems value="#{politicaEmprestimoMBean.tiposPrazo}" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th>Quantidade de Empr�stimos:</th>
								<td> 
									<h:inputText id="inputTextQtdEmprestimosAtrasados" 
											value="#{suspensaoUsuarioBibliotecaMBean.qtdEmprestimosCalculoManual}" size="1"  maxlength="2" 
											onkeyup="return formatarInteiro(this);"/> 
								</td>
							</tr>
							
							
							<tr>
								<th>Quantidade de Dias em Atraso: </th> <td> ${suspensaoUsuarioBibliotecaMBean.qtdDiasEmAtrasoCalculoManual} </td>
							</tr>
							
							<tr>
								<th>Quantidade de Dias Suspenso: </th> <td> ${suspensaoUsuarioBibliotecaMBean.qtdDiasSuspensoCalculoManual}  </td>
							</tr>
							
							<tr>
								<th>Data final da Suspens�o: </th> <td style="font-weight: bold;"> <ufrn:format type="data" valor="${suspensaoUsuarioBibliotecaMBean.dataFinalSuspensaoCalculoManual}" />  </td>
							</tr>
							
							<tfoot>
								<tr>
									<td colspan="2" style="text-align: center;">
										<a4j:commandButton id="cmdButtonCalcularSuspensaoManual" value="Calcular" 
												actionListener="#{suspensaoUsuarioBibliotecaMBean.calcularDataSuspensaoManual}"
												reRender="divCalculadora"/>
									</td>			
								</tr>
							</tfoot>
							
						</table>
						</t:div>
					</td>
				</tr>
				
				
				<tr>
					<th style="width:25%;">Data Inicial:</th>
					<td style="width:50px;">
						<ufrn:format type="data" valor="${suspensaoUsuarioBibliotecaMBean.obj.dataInicio}" />
					</td>
	
					<th class="obrigatorio">Data Final:</th>
					<td style="width:35%;">
						<t:inputCalendar value="#{suspensaoUsuarioBibliotecaMBean.obj.dataFim}" 
								size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
								renderPopupButtonAsImage="true"
								onkeypress="return formataData(this, event)" />
					</td>
				</tr>
				
				<tr>
					<th class="obrigatorio">Motivo da Suspens�o:</th> 
					<td colspan="3">
						<t:inputTextarea id="textAreaMotivoSuspensao" value="#{suspensaoUsuarioBibliotecaMBean.obj.motivoCadastro}"  cols="70" rows="4" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);"/>
					</td>
				</tr>	
				<tr>
					<th>Caracteres Restantes:</th>
					<td colspan="3">
						<span id="quantidadeCaracteresDigitados">200</span>
					</td>
				</tr>
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4" align="center">
						<h:commandButton value="#{suspensaoUsuarioBibliotecaMBean.confirmButton}" action="#{suspensaoUsuarioBibliotecaMBean.cadastrar}"/>
						<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
	</h:form>

</f:view>


<script language="JavaScript">
 
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>