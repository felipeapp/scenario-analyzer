<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style type="text/css">
div.opcoes { margin: 5px 0; }
div.opcoes a { font-size: 0.9em; }
tr.alunoSelecionado { background: #FF8888; }

h3 { text-align: center; font-variant: small-caps; border-bottom: 1px solid #BBB; margin-bottom: 5px;}
#relatorio th { font-weight: bold; }
#relatorio .linha { border-bottom: 1px dashed #BBB; }
#relatorio table tr td, #relatorio table tr th { font-size: 0.9em }
#relatorio .nota { width: 48px; text-align: center; }
#relatorio .situacao { width: 85px; text-align: center; }

#identificacao {border-bottom: 1px solid #CCC; width: 100%;}
#identificacao td { font-size: 1.1em; padding: 2px; }
</style>

<f:view>
	<h:outputText value="#{ relatorioNotasAluno.create }"/>
	<h2> Relatório de Entrada de Notas</h2>
	<div id="parametrosRelatorio">
	<table >
		<tr>
			<th> Ano-Período: </th>
			<td> ${ relatorioEntradaNotasMBean.ano }.${ relatorioEntradaNotasMBean.periodo } </td>
		</tr>
		<tr>
			<th> Módulo: </th>
			<td> ${relatorioEntradaNotasMBean.modulo.descricao} </td>
		</tr>
	</table>
	</div>
	<br/>

	<div class="notas" style="clear: both;">
	<c:set var="cod_turma" value=""/>
	<c:forEach var="linha" items="${ relatorioEntradaNotasMBean.linhas }" varStatus="i">

		<c:if test="${ linha.turma.codigo != cod_turma }">
		<c:set var="cod_turma" value="${linha.turma.codigo}"/>

		<c:if test="${ not empty cod_turma }">
		</tbody>
		</table>
		<br/>
		</c:if>

		<table class="tabelaRelatorio" width="100%">
		<caption>Turma ${ cod_turma }</caption>
			<thead>
				<tr>
					<th width="7%" style="text-align: center;">Código</th>
					<th>Disciplina</th>
					<th style="text-align: right;" class="nota" nowrap="nowrap"> Matriculados</th>
					<th class="nota" nowrap="nowrap"> Unidade 1</th>
					<th class="nota" nowrap="nowrap"> Unidade 2</th>
					<th class="nota" nowrap="nowrap"> Unidade 3</th>
					<th style="text-align: left;" class="situacao">Situação</th>
				</tr>
			</thead>
			<tbody>
		</c:if>

		<tr class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' } linha">
			<td style="text-align: center;" nowrap="nowrap">${ linha.turma.disciplina.codigo }</td>
			<td nowrap="nowrap" style="border-right: 1px solid #888;">${ linha.turma.disciplina.detalhes.nome }</td>
			
			<td style="border-right: 1px solid #888; text-align: right;" >
		  		${ linha.matriculados }
			</td>
			<td class="nota" style="border-right: 1px solid #888;">
				<c:if test="${linha.unidade1 == 0}">
					<img src="/sigaa/img/check.png" alt="OK" title="Todos os alunos possuem nota nesta unidade."/>
				</c:if>
				<c:if test="${linha.unidade1 != 0}">
					<img src="/sigaa/img/delete.png" alt="Pend." title="${linha.unidade1} aluno(s) sem nota."/>
				</c:if>
			</td>
			<td class="nota" style="border-right: 1px solid #888;">
		  		<c:if test="${linha.unidade2 == 0}">
					<img src="/sigaa/img/check.png" alt="OK" title="Todos os alunos possuem nota nesta unidade."/>
				</c:if>
				<c:if test="${linha.unidade2 != 0}">
					<img src="/sigaa/img/delete.png"  alt="Pend." title="${linha.unidade2} aluno(s) sem nota."/>
				</c:if>
			</td>
			<td class="nota" style="border-right: 1px solid #888;">
		  		<c:if test="${linha.unidade3 == 0}">
					<img src="/sigaa/img/check.png" alt="OK" title="Todos os alunos possuem nota nesta unidade." />
				</c:if>
				<c:if test="${linha.unidade3 != 0}">
					<img src="/sigaa/img/delete.png"  alt="Pend." title="${linha.unidade3} aluno(s) sem nota."/>
				</c:if>
			</td>
			<td class="situacao" style="text-align: left;">
		  		${ linha.turma.situacaoTurma.descricao }
			</td>
		</tr>
		</c:forEach>
		</tbody>
		</table>
	</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
