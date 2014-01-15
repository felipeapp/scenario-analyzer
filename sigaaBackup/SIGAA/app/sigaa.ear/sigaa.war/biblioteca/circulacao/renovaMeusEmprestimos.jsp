<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema /> &gt; Renovar Empréstimos</h2>
	
		<div class="descricaoOperacao" style="width:90%;">
			<p>Caro usuário, a lista abaixo apresenta todos os seus empréstimos renováveis.</p>
			<p> Selecione os empréstimos que deseja renovar e clique em "Renovar Empréstimos Selecionados". </p>
			<p><strong>Atenção:</strong> Apenas os empréstimos selecionados serão renovados. </p>
		</div>
	
	
	<h:form id="formularioRenovamaMeusEmprestimos">
	
		<%-- Passa alguns parâmetros no request para depois ter como verificar nos log por onde o usuário passou se ele vim afirmar que renovou os emprétimos --%>
		<h:inputHidden id="inputHiddenMensagemPaginaVisualiza" value="!!! USUÁRIO SUBMETEU A PÁGINA QUE SELECIONA OS SEUS EMPRÉSTIMOS RENOVÁVEIS, VERIFICAR OS ONs !!!"></h:inputHidden>

		<%-- Mantém a lista de empréstimos entre requisições --%>
		<a4j:keepAlive beanName="meusEmprestimosBibliotecaMBean" />


		
		<%-- 
		* Carrega os empréstimos do usuário na página.  
		* Utilizado porque o usuário também acessa a página diretamente quando vem redirecionado da parte pública do sistema 
		--%>
		${meusEmprestimosBibliotecaMBean.carregarEmprestimosAtivosRenovaveis}	



		<c:if test="${not empty meusEmprestimosBibliotecaMBean.emprestimosEmAbertoRenovaveis}">


			<table class="listagem">
				<caption>Empréstimos Ativos Renováveis (${fn:length(meusEmprestimosBibliotecaMBean.emprestimosEmAbertoRenovaveis)})</caption>
				<thead>
					<tr>
						<th style='text-align: center;'><input type="checkbox" onclick="selecionarTodosEmprestimos(this);" alt="Renovar todos os emprestimos" ></th>
						<th>Informações do Material</th>
						<th style='text-align: center;'>Data do Empréstimo</th>
						<th>Tipo do Empréstimos</th>
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
							<h:commandButton value="Renovar Empréstimos Selecionados" id="cmdButtonConfirmarRenovacaoMeusEmprestimos" action="#{meusEmprestimosBibliotecaMBean.renovarEmprestimos}" onclick="return confirm('Confirma a renovação dos empréstimos selecionados ? ');" />
							<h:commandButton value="Cancelar" id="cmdButtonCancelaRenovacaoMeusEmprestimos" action="#{meusEmprestimosBibliotecaMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>

		<c:if test="${empty meusEmprestimosBibliotecaMBean.emprestimosEmAbertoRenovaveis }">
			<div style="margin-top: 30px; color: red; text-align: center">
				Você não possui empréstimos ativos renováveis
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