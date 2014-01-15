<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />  <%-- Caso o usuário utilize o botão voltar --%>
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />

	<h2><ufrn:subSistema /> &gt; Comunicar Material Perdido</h2>
	
	<div class="descricaoOperacao" style="width:80%;">
		<p>Utilize este formulário para comunicar a perda de um material emprestado por um usuário da biblioteca.</p>
		<p>Por padrão, o sistema sugere um prazo para a reposição de um livro de 30 dias, mas esse prazo pode ser alterado.</p>
		<p> <strong>Observação: </strong>O prazo de punição referente ao empréstimo do material perdido não será contado entre os dias que o prazo para reposição vigorá. </p>
	</div>
	
	<%--  Parte onde o usuário visualiza o comprovante da comunicação da perda   --%>
	
	<c:if test="${comunicarMaterialPerdidoMBean.habilitaComprovante}">
		<h:form>
			<table  class="subFormulario" align="center" style="width: 70%;">
				<caption style="text-align: center;">Impressão Comprovante</caption>
				<tr>
				<td width="8%" valign="middle" align="center">
					<html:img page="/img/warning.gif"/>
				</td>
				<td valign="middle" style="text-align: justify">
					Por favor imprima o comprovante da comunicação clicando no ícone ao lado.
				</td>
				<td>
					<table>
						<tr>
							<td align="center">
						 		<h:graphicImage url="/img/printer_ok.png" />
						 	</td>
						 </tr>
						 <tr>
						 	<td style="font-size: medium;">
						 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{comunicarMaterialPerdidoMBean.telaComprovanteComunicacaoMaterialPerdido}"  />
						 	</td>
						 </tr>
					</table>
				</td>
				</tr>
			</table>
		<br/>
		</h:form>
	</c:if>
	
	
	
	
	<c:if test="${comunicarMaterialPerdidoMBean.todosMateriasEmprestadosEstaoPeridos}">
		<h:form>
			<table  class="subFormulario" align="center" style="width: 70%;">
				<caption style="text-align: center;">Cancelamento de Reservas</caption>
				<tr>
					<td width="8%" valign="middle" align="center">
						<html:img page="/img/warning.gif"/>
					</td>
					<td>
						<h:commandLink value="Clique aqui para cancelar as reservas existentes para o Título do material" 
							style="color: red;"
							action="#{comunicarMaterialPerdidoMBean.cancelarReservasDoTitulo}"  
							title="Clique aqui para cancelar as reservas existentes para o Título do material"    />
					</td>
				</tr>
			</table>
		<br/>
		</h:form>
	</c:if>
	
	
	<h:form id="form">

		<table class="formulario" width="80%">
			<caption>Comunicar Material Perdido</caption>

			<tr>
				<td colspan="5">
					<c:set var="_infoUsuarioCirculacao" value="${comunicarMaterialPerdidoMBean.informacaoUsuario}" scope="request"/>
					<c:set var="_mostrarFoto" value="${false}" scope="request"/>
					<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
				</td>
			</tr>
			

			<tr>
				<th style="width: 15%; font-weight: bold;">Código de Barras:</th>
				<td colspan="4">${comunicarMaterialPerdidoMBean.emprestimo.material.codigoBarras}</td>
			</tr>
			
			<tr>
				<th style="font-weight: bold;">Descrição:</th>
				<td colspan="4">${comunicarMaterialPerdidoMBean.emprestimo.material.informacao}</td>
			</tr>
			
			<tr style="height: 40px;">
				<th style="font-weight: bold;">Prazo Atual:</th>
				<td colspan="4"><ufrn:format type="dataHora" valor="${comunicarMaterialPerdidoMBean.emprestimo.prazo}"/> </td>
			</tr>
			
			
			<tr>
				<th class="obrigatorio">Novo Prazo:</th>
				<td colspan="4" style="width: 20%;"> 
					<t:inputCalendar id="Prazo" value="#{comunicarMaterialPerdidoMBean.novoPrazo}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" 
						disabled="#{comunicarMaterialPerdidoMBean.habilitaComprovante}"/>
				</td>
				
			</tr>

			<tr>
				<th class="obrigatorio">Justificativa:</th>
				<td colspan="4">
					<h:inputTextarea value="#{comunicarMaterialPerdidoMBean.motivo}" rows="5" cols="75" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);" 
						disabled="#{comunicarMaterialPerdidoMBean.habilitaComprovante}"/>
				</td>
			</tr>

			<tr>
				<th colspan="2">Caracteres Restantes:</th>
				<td colspan="3">
					<span id="quantidadeCaracteresDigitados">200</span>/200
				</td>
			</tr>
			

			<tfoot>
				<tr>
					<td colspan="5" align="center">
					
						<h:commandButton value="#{comunicarMaterialPerdidoMBean.confirmButton}" action="#{comunicarMaterialPerdidoMBean.comunicar}" id="acao" disabled="#{comunicarMaterialPerdidoMBean.habilitaComprovante}"/>
						
						<%-- Não criou nenhuma nova comunicação, então pode apenas voltar para a página anterior --%>
						<c:if test="${! comunicarMaterialPerdidoMBean.habilitaComprovante}">
							<h:commandButton value="Cancelar" action="#{comunicarMaterialPerdidoMBean.telaListaEmprestimosAtivoUsuarioParaComunicarMaterialPerdido}" immediate="true" onclick="#{confirm}"  />
						</c:if>
						
						<%-- Realizou nova comunicação, então precisa atualizar as inforções do usuário. --%>
						<c:if test="${comunicarMaterialPerdidoMBean.habilitaComprovante}">
							<h:commandButton value="Cancelar" action="#{comunicarMaterialPerdidoMBean.selecionouUsuarioBuscaPadrao}" immediate="true" onclick="#{confirm}" />
						</c:if>
						
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>

</f:view>


<script type="text/javascript">

function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>