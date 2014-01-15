<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
<a4j:keepAlive beanName="solicitacaoBolsasReuniBean" />

<%-- 
<h2> <ufrn:subSistema /> &gt; Solicita��o de Bolsas REUNI &gt; Plano de Trabalho</h2>
--%>	
<h2>Plano de Trabalho de Bolsista Reuni</h2>

<%--	
<table class="visualizacao" style="width: 100%">
	<caption> Dados do Plano de trabalho </caption>
	<tr>
		<th> Edital: </th>
		<td>
			<c:choose>
				<c:when test="${not empty planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital}">
					<a href="${ctx}/verArquivo?idArquivo=${planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital}&key=${ sf:generateArquivoKey(planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital) }" target="_blank">
						${ planoTrabalhoReuniBean.obj.solicitacao.edital }
					</a> 
				</c:when>
				<c:otherwise>
					${ planoTrabalhoReuniBean.obj.solicitacao.edital }
				</c:otherwise>
			</c:choose>
		
		</td>
	</tr>	
	<tr>
		<th> Programa de P�s-Gradua��o: </th>
		<td> <h:outputText value="#{  planoTrabalhoReuniBean.obj.solicitacao.programa }"/> </td>
	</tr>
	<tr>
		<th> Status da Solicita��o: </th>
		<td> ${  planoTrabalhoReuniBean.obj.solicitacao.descricaoStatus } </td>
	</tr>
	
</table>
--%>

<table class="listagem, tabelaRelatorio" width="100%">
	<tbody>
	
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Dados Gerais</h3>
		</td>
	</tr>
	
	<tr>
		<th> Edital: </th>
		<td>
			<c:choose>
				<c:when test="${not empty planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital}">
					<a href="${ctx}/verArquivo?idArquivo=${planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital}&key=${ sf:generateArquivoKey(planoTrabalhoReuniBean.obj.solicitacao.edital.idArquivoEdital) }" target="_blank" id="verArqDoEdital">
						${ planoTrabalhoReuniBean.obj.solicitacao.edital }
					</a> 
				</c:when>
				<c:otherwise>
					${ planoTrabalhoReuniBean.obj.solicitacao.edital }
				</c:otherwise>
			</c:choose>
		
		</td>
	</tr>	
	<tr>
		<th> Programa de p�s-gradua��o: </th>
		<td> <h:outputText value="#{  planoTrabalhoReuniBean.obj.solicitacao.programa }"/> </td>
	</tr>
	<tr>
		<th> Status da solicita��o: </th>
		<td> ${  planoTrabalhoReuniBean.obj.solicitacao.descricaoStatus } </td>
	</tr>
	
	<tr>
		<th width="30%">Linha de a��o:</th>
		<td>${planoTrabalhoReuniBean.obj.linhaAcao}</td>
	</tr>
	
	<c:if test="${not empty planoTrabalhoReuniBean.obj.areaConhecimento}">
	<tr>
		<th>�rea de conhecimento:</th>
		<td>${planoTrabalhoReuniBean.obj.areaConhecimento.denominacao}</td>
	</tr>
	</c:if>
	
	<tr>
		<th>N� de alunos beneficiados:</th>
		<td>${planoTrabalhoReuniBean.obj.numeroAlunosGraduacaoBeneficiados}</td>
	</tr>
	<tr>
		<th>N�vel:</th>
		<td>${planoTrabalhoReuniBean.obj.nivelDescricao}</td>
	</tr>
	
	<c:if test="${not empty planoTrabalhoReuniBean.obj.componenteCurricular}">
		<tr>
			<th>Componente Curricular:</th>
			<td>${planoTrabalhoReuniBean.obj.componenteCurricular}</td>
		</tr>
	</c:if>
	
	<c:if test="${not empty planoTrabalhoReuniBean.obj.justificativaComponenteCurricular}">
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Justificativa</h3>
		</td>
	</tr>
	
	<tr>
		<td colspan="2" class="texto" style="text-align: justify;">${planoTrabalhoReuniBean.obj.justificativaComponenteCurricular}</td>
	</tr>
	</c:if>
	
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Objetivos</h3>
		</td>
	</tr>
	
	<tr>
		<td colspan="2" class="texto" style="text-align: justify;">${planoTrabalhoReuniBean.obj.objetivos}</td>
	</tr>
	
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Cursos</h3>
		</td>
	</tr>
	
	<c:if test="${not empty planoTrabalhoReuniBean.obj.cursos}">
	<c:forEach items="${planoTrabalhoReuniBean.obj.cursos}" var="_curso">
	<tr>
		<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;${_curso}</td>
	</tr>
	</c:forEach>
	</c:if>
	
	<c:if test="${empty planoTrabalhoReuniBean.obj.cursos}">
	<tr>
		<td colspan="2" style="text-align: center; color: red;">N�o h� cursos de gradua��o associados a este plano de trabalho.</td>
	</tr>
	</c:if>
	
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Docentes</h3>
		</td>
	</tr>
	
	<c:if test="${not empty planoTrabalhoReuniBean.obj.docentes}">
	<c:forEach items="${planoTrabalhoReuniBean.obj.docentes}" var="_docente">
	<tr>
		<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;${_docente}</td>
	</tr>
	</c:forEach>
	</c:if>
	
	<c:if test="${empty planoTrabalhoReuniBean.obj.docentes}">
	<tr>
		<td colspan="2" style="text-align: center; color: red;">N�o h� docentes associados a este plano de trabalho.</td>
	</tr>
	</c:if>
	
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Formas de atua��o reuni</h3>
		</td>
	</tr>
	
	<c:forEach items="${planoTrabalhoReuniBean.obj.formasAtuacao}" var="_formaAtuacao">
	<tr>
		<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;${_formaAtuacao.descricao}</td>
	</tr>
	</c:forEach>
	
	<c:if test="${not empty planoTrabalhoReuniBean.obj.outrasFormasAtuacao}">
		<tr>
			<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;${planoTrabalhoReuniBean.obj.outrasFormasAtuacao}</td>
		</tr>	
	</c:if>
	
	<c:if test="${ acesso.ppg }">
		<tr class="subFormulario">
			<td colspan="2" class="subFormulario">
				<h3 class="tituloTabelaRelatorio">Avalia��o</h3>
			</td>
		</tr>
		
		<tr><td colspan="2">
			<table border="1">
				<tr>
					<td></td>
					<td style="font-weight: bold;">Crit�rio</td>
					<td style="text-align: right; font-weight: bold;">Peso</td>
					<td style="text-align: right; font-weight: bold;">Nota</td>
					<td style="text-align: right; font-weight: bold;">Pontua��o</td>
				</tr>
				<tr>
					<td>A</td>
					<td>Atendimento aos objetivos do edital, de acordo com as linhas de a��o do item 2</td>
					<td style="text-align: right;">2</td>
					<td style="text-align: right;"></td>
					<td style="text-align: right;"></td>
				</tr>
				<tr>
					<td>B</td>
					<td>Qualidade do plano quanto ao seu potencial de desenvolver novas metodologias de 
					ensino-aprendizagem e de contribuir para a melhoria do ensino de gradua��o</td>
					<td style="text-align: right;">3</td>
					<td style="text-align: right;"></td>
					<td style="text-align: right;"></td>
				</tr>
				<tr>
					<td>C</td>
					<td>Contribui��o na consolida��o do programa de p�s-gradua��o</td>
					<td style="text-align: right;">3</td>
					<td style="text-align: right;"></td>
					<td style="text-align: right;"></td>
				</tr>	
				<tr>
					<td>D</td>
					<td>N�mero previsto de alunos de gradua��o beneficiados com a proposta</td>
					<td style="text-align: right;">2</td>
					<td style="text-align: right;"></td>
					<td style="text-align: right;"></td>
				</tr>
				<tr>
					<td colspan="4"></td>
					<td>&nbsp;</td>
				</tr>	
			</table>
		</td></tr>
	</c:if>
	
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Indica��o</h3>
		</td>
	</tr>
	
	<c:if test="${not empty planoTrabalhoReuniBean.indicacaoBolsista}">
		<tr>
			<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;${planoTrabalhoReuniBean.indicacaoBolsista.discente.matricula} - ${planoTrabalhoReuniBean.indicacaoBolsista.discente.pessoa.nome}</td>
		</tr>
	</c:if>
	
	<c:if test="${empty planoTrabalhoReuniBean.indicacaoBolsista}">
	<tr>
		<td colspan="2" style="text-align: center; color: red;">N�o h� indica��o a este plano de trabalho.</td>
	</tr>
	</c:if>	
	
	</tbody>
</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>