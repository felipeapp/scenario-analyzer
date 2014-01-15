<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

		<a4j:keepAlive beanName="multasUsuarioBibliotecaMBean" />
		<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

		<h2> <ufrn:subSistema /> &gt; Confirma��o Manual de Pagamento de Multas</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p> Caro usu�rio, por op��o � poss�vel confirmar no sistema o pagamento da multa e liberar o usu�rio para realizar novos empr�stimos.</p>
			<p> O usu�rio que estava multado ser� informado automaticamente sobre pagamento da multa.</p>
		</div>
		
		<div class="descricaoOperacao" style="width:85%;">
			<br/>
			<br/>
			<p> 
				<strong>
				Declaro para os devidos fim que estou confirmando o pagamento desta multa mediante a apresenta��o <br/> 
				do comprovante de pagamento da GRU com o N�MERO DE REFER�NCIA: ${multasUsuarioBibliotecaMBean.numeroReferencia}.
				<br/>
				<br/>
				</strong>
			</p>
			<br/>
			<br/>
		</div>
		
		<%-- Exibe as informa��es do usu�rio --%>
		<c:set var="_infoUsuarioCirculacao" value="${multasUsuarioBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
		
		
		<h:form id="formConfirmaPagamentoMulta">
		
		<%-- Dados para facilitar a auditoria caso alguem alege que confirmou o pagamento mas ele nao foi realizado --%>
		<input type="hidden" value="USUARIO ESTA NA PAGINA QUE CONFIRMA O PAGAMENTO" name="MENSAGEM_LOG" />
		<input type="hidden" value="${usuario.id}" name="ID_USUARIO_CONFIRMOU_PAGAMENTO" />
		<input type="hidden" value="${usuario.nome}" name="NOME_USUARIO_CONFIRMOU_PAGAMENTO" />
		
		<table class="formulario" style="margin-bottom:10px;width:90%;">
			
			<caption>Dados da Multa Selecionada </caption>
			
				
				<tbody>
					
					<tr>
						<th style="width: 20%"> Valor da multa: </th>
						<td> ${multasUsuarioBibliotecaMBean.obj.valorFormatado} </td>
						<th style="width: 20%"> Dados da GRU:</th>
						<td> ${multasUsuarioBibliotecaMBean.obj.infoIdentificacaoMultaGerada} </td>
					</tr>
					
					<tr>
						<th style="width: 15%"> Observa��o:</th>
						<td colspan="3"><h:inputTextarea id="motivo" value="#{multasUsuarioBibliotecaMBean.obj.observacaoPagamento}" rows="3" style="width:90%" 
													onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 300);" /> </td>
					</tr> 
					<tr>
						<th>Caracteres Restantes:</th>
						<td>
							<span id="quantidadeCaracteresDigitados">300</span>
						</td>
					</tr>
					
					<tr>
						<td colspan="4">
							<table style="width: 100%">
								<tr>
									<td style="font-weight: bold; font-size: 16px; text-align: center">  N�mero de Refer�ncia: ${multasUsuarioBibliotecaMBean.numeroReferencia}</td>
								</tr>
							</table>
						</td>
					</tr>
					
				</tbody>
				
			
				<tfoot>
					<tr>
						<td colspan="4">
							<h:commandButton id="cmdConfirmaPagamentoMulta"  value="Confirmar Pagamento no Sistema" action="#{multasUsuarioBibliotecaMBean.confirmarPagamento}"/>
							<h:commandButton id="cmdVoltar" value="<< Voltar" action="#{multasUsuarioBibliotecaMBean.telaListaMultasUsuario}" />
							<h:commandButton  id="cmdCancelar"  value="Cancelar" onclick="#{confirm}" action="#{multasUsuarioBibliotecaMBean.cancelar}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
			
				
		</table>
		
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