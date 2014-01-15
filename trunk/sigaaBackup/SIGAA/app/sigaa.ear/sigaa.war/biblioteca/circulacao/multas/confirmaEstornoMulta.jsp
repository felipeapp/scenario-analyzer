<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

		<a4j:keepAlive beanName="multasUsuarioBibliotecaMBean" />
		<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

		<h2> <ufrn:subSistema /> &gt; Estornar Multas Selecionada</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p> Caro usuário, </p>
			<p> Por esta opção pode-se estornar a multa do usuário, caso esse multa tenha sido gerada indevidamente.</p>
			<p>O usuário que estava multado será informado automaticamente sobre o estorno da multa.</p>
		</div>
		
			<%-- Exibe as informações do usuário --%>
		<c:set var="_infoUsuarioCirculacao" value="${multasUsuarioBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
		
		<h:form id="formConfirmaPagamentoMulta">
		
		<table class="formulario" style="margin-bottom:10px;width:90%;">
			<caption>Dados da Multa Selecionada </caption>
			
				
				<tbody>
					
					<tr>
						<th style="width: 15%"> Valor da multa: </th>
						<td> ${multasUsuarioBibliotecaMBean.obj.valorFormatado} </td>
						<th style="width: 15%"> Dados da multa:</th>
						<td> ${multasUsuarioBibliotecaMBean.obj.infoIdentificacaoMultaGerada} </td>
					</tr>
					
					<tr>
						<th class="required" style="width: 15%"> Motivo do Estorno:</th>
						<td colspan="3"><h:inputTextarea id="motivo" value="#{multasUsuarioBibliotecaMBean.obj.motivoEstorno}" rows="3" style="width:90%" 
													onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 300);" /> </td>
					</tr> 
					<tr>
						<th>Caracteres Restantes:</th>
						<td>
							<span id="quantidadeCaracteresDigitados">300</span>
						</td>
					</tr>
					
					<tr>
						<td colspan="4" style="text-align: center; font-weight: bold; color:red;">
							<c:if test="${multasUsuarioBibliotecaMBean.obj.gruJaFoiGerada}">
							Atenção: A GRU para o pagamento dessa multa já foi gerada, talvez ela já tenha sido paga!
							</c:if>
						</td>
					<tr>
					
					<tr>
						<td  colspan="4">
							<%-- Campo para o usuário informar a sua senha para confirmar o estorno das multas   --%>
							<c:set var="exibirApenasSenha" value="true" scope="request"/>
							<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
						</td>
					</tr>
					
				</tbody>
				
			
				<tfoot>
					<tr>
						<td colspan="4">
							<h:commandButton value="Confirmar Estorno" action="#{multasUsuarioBibliotecaMBean.estornar}" id="cmdEstornoMulta" onclick="return confirm('Confirma estorno da multa ? ');"/>
							<h:commandButton id="cmdVoltar" value="<< Voltar" action="#{multasUsuarioBibliotecaMBean.telaListaMultasUsuario}" />
							<h:commandButton  id="cmdCancelar"  value="Cancelar" onclick="#{confirm}" action="#{multasUsuarioBibliotecaMBean.cancelar}" immediate="true"/>
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