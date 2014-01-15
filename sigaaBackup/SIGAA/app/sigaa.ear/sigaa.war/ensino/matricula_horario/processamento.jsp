<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2> <ufrn:subSistema /> &gt; Processamento de Matr�cula </h2>

<h:form>
	<table class="formulario">
		<caption>Processamento de Matr�cula</caption>
		<tr>
			<td>
				<div class="descricaoOperacao">
					<p>
						Antes de efetuar o processamento de matr�cula verifique se:
					</p>
					<ul>
						<li>Todos os alunos possuem nota final no M�dulo B�sico;</li>
						<li>Todos os alunos efetuaram a Matr�cula On-Line.</li>
					</ul>
					<p>
						Em seguida, clique em <strong>Processar Matr�culas</strong> e aguarde alguns instantes. O sistema exibir� o resultado do processamento
						abaixo. Para matricular os alunos nas turmas clique em <strong>Confirmar Processamento de Matr�cula</strong>.
					</p>
				</div> 
			</td>
		</tr>
		<tfoot>
			<tr>
				<td align="center">
					<h:commandButton value="Processar Matr�culas" action="#{matriculaModuloAvancadoMBean.processar}" id="processar" />
					<h:commandButton value="Cancelar" action="#{matriculaModuloAvancadoMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
					<h:commandButton value="Confirmar Processamento de Matr�cula" action="#{matriculaModuloAvancadoMBean.confirmarProcessamento}" id="confirmarProcessamento" />
				</td>
			</tr>
		</tfoot>		
	</table>
	
	<br/><br/>

	<c:set var="turmasProcessadas" value="#{matriculaModuloAvancadoMBean.turmas}"/>

	<table class="listagem" width="100%">
		<caption>Turmas </caption>
		<c:forEach items="${turmasProcessadas}" var="turma">
			<tr>
				<td>
					<table class="subformulario" width="100%">
					<caption>${ turma.disciplina.nome } - ${ turma.codigo }</caption>
					<c:forEach items="${turma.matriculasDisciplina}" var="mat" varStatus="i">
						<tr>
							<td>${ i.index + 1}</td>
							<td>${ mat.discente.matriculaNome }</td>
						</tr>
					</c:forEach>
					</table>
				</td>
			</tr>
		</c:forEach>
	</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>