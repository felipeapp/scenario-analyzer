<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ajaxtags/scriptaculous.js"> </script>

 
<h2> <ufrn:subSistema /> > Resumo </h2>
	<table width="700" class="formulario">
	<caption>Resumo da Estrutura Curricular</caption>
	<tbody>
		<tr>
		<th width="200">
		Código da Estrutura:
		</th>
		<td colspan="4">
		${ estruturaCurTecnicoForm.obj.codigo }
		</td>
		</tr>

		<tr>
		<th>
		Curso:
		</th>
		<td colspan="4">
		${ estruturaCurTecnicoForm.obj.cursoTecnico.nome }
		</td>
		</tr>

		<tr>
		<th>
		Prazos de Conclusão:
		</th>
		<th width="100">
		Mínimo:
		</th>
		<td width="100">
		${ estruturaCurTecnicoForm.obj.prazoMinConclusao } ${ estruturaCurTecnicoForm.obj.unidadeTempo.descricao } (s)
		</td>
		<th width="100">
		Máximo:
		</th>
		<td>
		${ estruturaCurTecnicoForm.obj.prazoMaxConclusao } ${ estruturaCurTecnicoForm.obj.unidadeTempo.descricao } (s)
		</td>
		</tr>

		<tr>
		<th>
		Carga Horária:
		</th>
		<td colspan="4">
		${ estruturaCurTecnicoForm.obj.cargaHoraria }
		</td>
		</tr>

		<tr>
		<th>
		Ano - Período de <br>Entrada em Vigor:
		</th>
		<td>
		${ estruturaCurTecnicoForm.obj.anoEntradaVigor } - ${ estruturaCurTecnicoForm.obj.periodoEntradaVigor }
		</td>
		<th>
		Ativa:
		</th>
		<td colspan="2">
		<ufrn:format type="bool_sn" valor="${ estruturaCurTecnicoForm.obj.ativa }" />
		</td>
		</tr>

		<tr>
		<th>
		Turno:
		</th>
		<td colspan="4">
		${ estruturaCurTecnicoForm.obj.turno.descricao }
		</td>
		</tr>

		<%-- LISTA DE MÓDULOS --%>
		<c:if test="${ empty estruturaCurTecnicoForm.obj.modulosCurriculares }">
		<tr><td colspan="5" align="center"><i>Nenhum módulo adicionado.</i></td></tr>
		</c:if>
		<c:if test="${ not empty estruturaCurTecnicoForm.obj.modulosCurriculares }">
		<tr>
		<td colspan="5">
			<table class="subFormulario" width="99%">
				<thead>
				<td>
				Módulos cadastrados &nbsp;&nbsp;&nbsp;
				<span style="font-size: 9px; font-weight: normal">
				[ <html:link href="#" onclick="toggleClass('disciplinas')">mostrar /
				esconder</html:link> ]
				todas as disciplinas dos módulos
				</span>
				</td>
				<td width="50">C.H.</td>
				<td width="30">Pr. Oferta</td>
				</thead>

				<tbody>
				<c:forEach items="${estruturaCurTecnicoForm.obj.modulosCurriculares}" var="moduloCurricular">
					<tr>
					<td>
					<html:link href="#" onclick="toggleDisciplinas('disciplinas${moduloCurricular.modulo.id}')">
					+ ${moduloCurricular.modulo.descricao}
					</html:link>
					</td>
					<td align="center">${moduloCurricular.modulo.cargaHoraria}</td>
					<td align="center">${moduloCurricular.periodoOferta}</td>
					</tr>

					<tr class="disciplinas${moduloCurricular.modulo.id}" >
					<td colspan="3" style="padding-left: 30px;" >
					<table width="100%" class="disciplinas" id="disciplinas${moduloCurricular.modulo.id}">
						<tr>
						<td colspan="3" style="font-size: 9px;">
						<b>Disciplinas:</b>
						</td>
						</tr>
						<c:forEach items="${moduloCurricular.modulo.disciplinas}" var="disciplina">
							<tr>
							<td style="font-size: 9px;">
							${disciplina.descricao}</td>
							</tr>
						</c:forEach>
					</table>
					</tr>

				</c:forEach>

			</table>
		</td>
		</tr>
		</c:if>

		<c:if test="${ empty estruturaCurTecnicoForm.obj.disciplinasComplementares }">
		<tr><td colspan="5" align="center"><i>Nenhuma disciplina complementar adicionada.</i></td></tr>
		</c:if>
		<c:if test="${ not empty estruturaCurTecnicoForm.obj.disciplinasComplementares }">
		<%-- LISTA DE DISCIPLINAS COMPLEMENTARES --%>
		<tr>
		<td colspan="5">
			<table class="subFormulario" width="100%">
				<thead>
				<td>Disciplinas Eletivas Cadastradas</td>
				<td width="50">C.H.</td>
				<td width="30">Pr. Oferta</td>
				</thead>

				<tbody>
				<c:forEach items="${estruturaCurTecnicoForm.obj.disciplinasComplementares}" var="disciplinaComplementar">
					<tr>
					<td>${disciplinaComplementar.disciplina.codigo} - ${disciplinaComplementar.disciplina.nome}</td>
					<td align="center">${disciplinaComplementar.disciplina.chTotal}</td>
					<td align="center">${disciplinaComplementar.periodoOferta}</td>
					</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
		</c:if>

	</tbody>

	<tfoot>
		<tr>
		<td colspan="5">
		<html:form action="/ensino/tecnico/estruturaCurricular/wizard"  method="post"  >

		<c:if test="${ param.dispatch != 'visualizar' }">
			<html:button dispatch="chamaModelo">Confirmar </html:button>
		</c:if>
		<html:button dispatch="cancelar"><< voltar</html:button>
		<c:if test="${ param.dispatch != 'remove' and param.dispatch != 'visualizar'}">
			<br><br>
			<html:button view="dadosGerais">&lt;&lt; Dados Gerais</html:button>
			<html:button view="modulos">&lt;&lt; Módulos</html:button>
			<html:button view="disciplinas">&lt;&lt; Disciplinas</html:button>
		</c:if>


		</html:form>
		</td>
		</tr>
	</tfoot>

	</table>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>