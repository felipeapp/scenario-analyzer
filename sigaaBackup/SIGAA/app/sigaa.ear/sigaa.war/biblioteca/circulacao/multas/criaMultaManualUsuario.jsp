<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

		<a4j:keepAlive beanName="multasUsuarioBibliotecaMBean" />
		<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
		
		<h2> <ufrn:subSistema /> &gt; Cria Nova Multa para o Usuário</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p>Caro usuário, </p>
			<p>Utilize este formulário para criar uma nova multa para o usuário selecionado. </p>
			<p>A partir do momento da em que a multa for criada, a sua GRU já estará disponível para ser impressa e paga.</p>
			<br/>
			<p><span style="font-weight: bold;">IMPORTANTE:</span> O campo <span style="font-weight: bold;font-style: italic;">Unidade de Recolhimento</span> se refere a unidade de custo que irá receber os créditos da GRU.<br/> 
				<ul>
				<li>Caso a sua Biblioteca seja uma unidade de recolhimento, selecione-a. </li>
				<li> Caso contrário, deixe esse campo em branco para que a GRU seja creditada na conta da Biblioteca Central do sistema.</li>
				</ul>
			</p>
			
		</div>
		
		
		<%-- Exibe as informações do usuário --%>
		<c:set var="_infoUsuarioCirculacao" value="${multasUsuarioBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
		
		<h:form id="formularioCriaMulta">
		
		
			<table class="formulario" style="margin-bottom:10px;width:90%;">
				<caption>Dados da Multa</caption>
				<tbody>
					
					<tr>
						<td colspan="4" style="color: red; font-weight: bold; text-align: center;">
							<t:div id="divMensagemAlertaMulta" rendered="#{multasUsuarioBibliotecaMBean.valorTotalMultas > 0 }">
								O usuario está multado em <ufrn:format type="moeda" valor="${multasUsuarioBibliotecaMBean.valorTotalMultas}" />
							</t:div>
						</td>
					</tr>
					
					<tr>
						<td colspan="4">
							<t:div id="divCalculadora">
							<table id="tabelaCalculadora" class="subFormulario" style="width: 80%; margin-bottom: 20px; border: 1px solid #DEDFE3;">
								<caption> Calculadora dos Valores das Multas</caption>
								
								<tr>
									<td colspan="2">
										<div class="descricaoOperacao">
											<p>O cálculo leva em consideração que todos os empréstimos possuem o mesmo tipo de prazo.</p>
										</div>
									</td>
								</tr>
								
								<tr>
									<th width="40%">Prazo para Devolução:</th>
									<td> 
										<t:inputCalendar value="#{multasUsuarioBibliotecaMBean.prazoDevolucaoCalculoManual}" 
											size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
											renderPopupButtonAsImage="true"
											onkeypress="return formataData(this, event)" />
									</td>
								</tr>
								<tr>
									<th>Data da Devolução:</th>
									<td> 
										<t:inputCalendar value="#{multasUsuarioBibliotecaMBean.dataDevolucaoCalculoManual}" 
											size="10" maxlength="10" renderAsPopup="true"  popupDateFormat="dd/MM/yyyy" 
											renderPopupButtonAsImage="true"
											onkeypress="return formataData(this, event)" />
									</td>
								</tr>
								<tr>
									<th>Tipo do Prazo:</th>
									<td> 
										<h:selectOneMenu id="tipoPrazoEmprestimos" value="#{multasUsuarioBibliotecaMBean.tipoPrazoCalculoManual}">
											<f:selectItems value="#{politicaEmprestimoMBean.tiposPrazo}" />
										</h:selectOneMenu>
									</td>
								</tr>
								<tr>
									<th>Quantidade de Empréstimos:</th>
									<td> 
										<h:inputText id="inputTextQtdEmprestimosAtrasados" 
												value="#{multasUsuarioBibliotecaMBean.qtdEmprestimosCalculoManual}" size="1"  maxlength="2" 
												onkeyup="return formatarInteiro(this);"/> 
									</td>
								</tr>
								
								<tr>
									<th>Quantidade de Dias em Atraso: </th> <td> ${multasUsuarioBibliotecaMBean.qtdDiasEmAtrasoCalculoManual} </td>
								</tr>
								
								<tr>
									<th>Valor da Multa: </th> <td style="font-weight: bold;"> <ufrn:format type="moeda" valor="${multasUsuarioBibliotecaMBean.valorMultaCalculoManual}" />  </td>
								</tr>
								
								<tfoot>
									<tr>
										<td colspan="2" style="text-align: center;">
											<a4j:commandButton id="cmdButtonCalcularSuspensaoManual" value="Calcular" 
													actionListener="#{multasUsuarioBibliotecaMBean.calcularValorMultaManual}"
													reRender="divCalculadora"/>
										</td>			
									</tr>
								</tfoot>
								
							</table>
							</t:div>
						</td>
					</tr>
						
					<tr>
						<th class="required" style="width: 20%"> Valor da multa: </th>
						<td>
							<h:inputText id="valorMulta" value="#{multasUsuarioBibliotecaMBean.obj.valorAsDouble}" maxlength="6" size="10" 
							style="text-align: right" onkeydown="return(formataValor(this, event, 2))" onfocus="javascript:select()" >
								   <f:converter converterId="convertMoeda"/>
							</h:inputText>	
						</td>
					</tr>
					
					<tr>
						<th class="required" style="width: 20%"> Unidade de Recolhimento: </th>
						<td>
							<h:selectOneMenu id="comboBoxUnidadeRecolhimento" value="#{multasUsuarioBibliotecaMBean.obj.bibliotecaRecolhimento.id}" >
								<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
								<f:selectItems value="#{multasUsuarioBibliotecaMBean.allBibliotecasInternasAtivasComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					
					<tr>
						<th class="required" style="width: 20%"> Motivo da criação da multa:</th>
						<td> <h:inputTextarea id="motivoMulta" value="#{multasUsuarioBibliotecaMBean.obj.motivoCadastro}" cols="70" rows="3"
								onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);" />  </td>
					</tr>
					<tr>
						<th>Caracteres Restantes:</th>
						<td>
							<span id="quantidadeCaracteresDigitados">200</span>
						</td>
					</tr>
					
				</tbody>
		
		
				<tfoot>
					<tr>
						<td colspan="4" style="text-align: center;">
							<h:commandButton value="Criar Multa" action="#{multasUsuarioBibliotecaMBean.cadastrar}" id="cmdCriarMulta" />
							<h:commandButton value="<< Voltar" action="#{multasUsuarioBibliotecaMBean.telaListaMultasUsuario}" id="cmdVoltar" />
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{multasUsuarioBibliotecaMBean.cancelar}" immediate="true" id="cmdCancelar" />
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
