<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema /> &gt; Renovar Empr�stimos</h2>
	
		<div class="descricaoOperacao" style="width:90%;">
			<p>Caro usu�rio, a lista abaixo apresenta todos os seus empr�stimos renov�veis.</p>
			<p> Selecione os empr�stimos que deseja renovar e clique em "Renovar Empr�stimos Selecionados". </p>
			<p><strong>Aten��o:</strong> Apenas os empr�stimos selecionados ser�o renovados. </p>
		</div>
	
	
	<h:form id="formularioRenovamaMeusEmprestimos">
	
		<%-- Passa alguns par�metros no request para depois ter como verificar nos log por onde o usu�rio passou se ele vim afirmar que renovou os empr�timos --%>
		<h:inputHidden id="inputHiddenMensagemPaginaVisualiza" value="!!! USU�RIO SUBMETEU A P�GINA QUE SELECIONA OS SEUS EMPR�STIMOS RENOV�VEIS, VERIFICAR OS ONs !!!"></h:inputHidden>

		<%-- Mant�m a lista de empr�stimos entre requisi��es --%>
		<a4j:keepAlive beanName="meusEmprestimosBibliotecaMBean" />


		
		<%-- 
		* Carrega os empr�stimos do usu�rio na p�gina.  
		* Utilizado porque o usu�rio tamb�m acessa a p�gina diretamente quando vem redirecionado da parte p�blica do sistema 
		--%>
		${meusEmprestimosBibliotecaMBean.carregarEmprestimosAtivosRenovaveis}	



		<c:if test="${not empty meusEmprestimosBibliotecaMBean.emprestimosEmAbertoRenovaveis}">


			<table class="listagem">
				<caption>Empr�stimos Ativos Renov�veis (${fn:length(meusEmprestimosBibliotecaMBean.emprestimosEmAbertoRenovaveis)})</caption>
				<thead>
					<tr>
						<th style='text-align: center;'><input type="checkbox" onclick="selecionarTodosEmprestimos(this);" alt="Renovar todos os emprestimos" ></th>
						<th>Informa��es do Material</th>
						<th style='text-align: center;'>Data do Empr�stimo</th>
						<th>Tipo do Empr�stimos</th>
						<th style='text-align: center;'>Prazo</th>
					</tr>
					
				</thead>
				<tbody>
					<c:forEach items="#{meusEmprestimosBibliotecaMBean.emprestimosEmAbertoRenovaveis}" var="e" varStatus="status">
						<tr class='${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}'>
							<td style="text-align:center;">
								<h:selectBooleanCheckbox id="checkBoxSelecionaEmprestimo" value="#{e.selecionado}" label="renovar esse emprestimo"/>
							</td>
							<td style="width: 50%;">${e.material.informacao}</td>
							<td style="text-align:center">
								<h:outputText value="#{e.dataEmprestimo}" converter="convertData" />
							</td>
							<td  style="text-align:center">${e.politicaEmprestimo.tipoEmprestimo.descricao}</td>
							<td style="text-align:center">
								<h:outputText value="#{e.prazo}" converter="convertData">
									<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss" />
								</h:outputText>
							</td>
						</tr>

					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="5" style="text-align: center;">
							<h:commandButton value="Renovar Empr�stimos Selecionados" id="cmdButtonConfirmarRenovacaoMeusEmprestimos" action="#{meusEmprestimosBibliotecaMBean.renovarEmprestimos}" onclick="return confirm('Confirma a renova��o dos empr�stimos selecionados ? ');" />
							<h:commandButton value="Cancelar" id="cmdButtonCancelaRenovacaoMeusEmprestimos" action="#{meusEmprestimosBibliotecaMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>

		<c:if test="${empty meusEmprestimosBibliotecaMBean.emprestimosEmAbertoRenovaveis }">
			<div style="margin-top: 30px; color: red; text-align: center">
				Voc� n�o possui empr�stimos ativos renov�veis
			</div>
		</c:if>
		
	</h:form>
	
</f:view>

<script type="text/javascript">

	function selecionarTodosEmprestimos(chk){
		for (i=0; i<document.formularioRenovamaMeusEmprestimos.elements.length; i++)
		      if(document.formularioRenovamaMeusEmprestimos.elements[i].type == "checkbox")
		         document.formularioRenovamaMeusEmprestimos.elements[i].checked= chk.checked;
	}

</script>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>