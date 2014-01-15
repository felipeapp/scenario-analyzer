<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages />
	<h2 class="title"><ufrn:subSistema /> > Visualiza��o de Atividades Orientadas</h2>

	<table class="visualizacao" width="80%">
	<caption>Dados da Atividade Orientada</caption>
	<tbody>
		<c:if test="${not empty orientacaoAtividade.obj.orientador}">
			<tr>
				<th>Orientador:</th>
				<td>
					${orientacaoAtividade.obj.orientador.nome}
				</td>
			</tr>
		</c:if>
		<c:if test="${not empty orientacaoAtividade.obj.orientadorExterno}">
			<tr>
				<th>Orientador Externo:</th>
				<td>
					${orientacaoAtividade.obj.orientadorExterno.nome}
				</td>
			</tr>
		</c:if>
		<tr>
			<th width="30%">Atividade:</th>
			<td>
				${orientacaoAtividade.obj.registroAtividade.matricula.componenteCodigoNome}
			</td>
		</tr>
		<tr>
			<th>Ano-Per�odo:</th>
			<td>
				${orientacaoAtividade.obj.registroAtividade.matricula.ano}.${orientacaoAtividade.obj.registroAtividade.matricula.periodo}
			</td>
		</tr>
		<tr>
			<th>Supervisor:</th>
			<td>
				${orientacaoAtividade.obj.registroAtividade.supervisor}
			</td>
		</tr>
		<tr>
			<th>Discente:</th>
			<td>
				${orientacaoAtividade.obj.registroAtividade.matricula.discente.matriculaNome}
			</td>
		</tr>
		<tr>
			<th>Situa��o da Matr�cula:</th>
			<td>
				${orientacaoAtividade.obj.registroAtividade.matricula.situacaoMatricula.descricao}
			</td>
		</tr>
		<tr>
			<th>Resultado:</th>
			<td>
				${orientacaoAtividade.obj.registroAtividade.matricula.mediaFinal} 
			</td>
		</tr>
		<tr>
			<th>Carga Hor�ria:</th>
			<td>
				${orientacaoAtividade.obj.registroAtividade.matricula.componente.chTotal} horas
			</td>
		</tr>
		<c:if test="${not empty orientacaoAtividade.teseOrientada}">
			<tr><td colspan="2" class="subFormulario">Produ��o Associada: Tese Orientada</td></tr>
			<tr>
				<td width="20%">T�tulo:</td>
				<td>${orientacaoAtividade.teseOrientada.titulo}</td>
			</tr>
			<tr>
				<td>Programa:</td>
				<td>${orientacaoAtividade.teseOrientada.programaPos.nome}</td>
			</tr>
			<tr>
				<td>�rea de Conhecimento:</td>
				<td>${orientacaoAtividade.teseOrientada.area.nome}</td>
			</tr>
			<tr>
				<td>Sub-�rea de Conhecimento:</td>
				<td>${orientacaoAtividade.teseOrientada.subArea.nome}</td>
			</tr>
			<tr>
				<td>Tipo de Orienta��o:</td>
				<td>${orientacaoAtividade.teseOrientada.tipoOrientacao.descricao}</td>
			</tr>
		</c:if>
		<c:if test="${not empty orientacaoAtividade.estagios}">
		<tr><td colspan="2" class="subFormulario">Produ��o Associada: Est�gios</td></tr>
		<c:forEach var="item" items="${orientacaoAtividade.estagios}">
			<tr>
				<th width="20%">Nome do Projeto:</th>
				<td>${item.nomeProjeto}</td>
			</tr>
			<tr>
				<th>Per�odo:</th>
				<td><ufrn:format type="data" valor="${item.periodoInicio}" /> - <ufrn:format type="data" valor="${item.periodoFim}" /></td>
			</tr>
			<tr>
				<th>CH Semanal:</th>
				<td>${item.chSemanal}</td>
			</tr>
			<tr>
				<th>Entidade Financiadora:</th>
				<td>${item.entidadeFinanciadora.nome}</td>
			</tr>
			<tr>
				<th>�rea de Conhecimento:</th>
				<td>${item.area.nome}</td>
			</tr>
			<tr>
				<th>Sub-�rea de Conhecimento:</th>
				<td>${item.subArea.nome}</td>
			</tr>
		</c:forEach>
		</c:if>
		<c:if test="${not empty orientacaoAtividade.trabalhoFimCurso}">
		<tr><td colspan="2" class="subFormulario">Produ��o Associada: Trabalho de Fim de Curso</td></tr>
			<tr>
				<th width="20%">T�tulo:</th>
				<td>${orientacaoAtividade.trabalhoFimCurso.titulo}</td>
			</tr>
			<tr>
				<th>Tipo:</th>
				<td>${orientacaoAtividade.trabalhoFimCurso.tipoTrabalhoConclusao.descricao}</td>
			</tr>
			<tr>
				<th>Data da Defesa:</th>
				<td><ufrn:format type="data" valor="${orientacaoAtividade.trabalhoFimCurso.dataDefesa}"/></td>
			</tr>
			<tr>
				<th>Entidade Financiadora:</th>
				<td>${item.entidadeFinanciadora.nome}</td>
			</tr>
			<tr>
				<th>�rea de Conhecimento:</th>
				<td>${orientacaoAtividade.trabalhoFimCurso.area.nome}</td>
			</tr>
			<tr>
				<th>Sub-�rea de Conhecimento:</th>
				<td>${orientacaoAtividade.trabalhoFimCurso.subArea.nome}</td>
			</tr>
		</c:if>
		
	</tbody>
	</table>
	<br/>
	<p style="text-align: center"><a href="javascript:history.go(-1)"> << Voltar</a></p>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
