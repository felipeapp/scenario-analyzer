<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<html:form action="/ensino/latosensu/criarCurso" method="post">

<table class="formulario">
<caption> Proposta de Criação de Curso Lato Sensu </caption>
<tr>
	<td>
	<table class="subFormulario" width="100%">
	<caption>Identificação do Projeto</caption>
		<tr>
		<td><b>Instituição:</b></td>
		<td>${ configSistema['nomeInstituicao'] } - ${ configSistema['siglaInstituicao'] }</td>
		</tr>
		<tr>
		<td><b>Curso:</b></td>
		<td>${cursoLatoForm.obj.nome}</td>
		</tr>
		<tr>
		<td><b>Grande Área e Área do Conhecimento:</b></td>
		<td>${cursoLatoForm.obj.areaConhecimentoCnpq.grandeArea.nome}</td>
		</tr>
		<tr>
		<td><b>Unidade Responsável:</b></td>
		<td>${cursoLatoForm.obj.unidade.nome}</td>
		</tr>
		<tr>
		<td><b>Coordenador do Curso:</b></td>
		<td>${cursoLatoForm.coordenador.servidor.nome}</td>
		</tr>
		<tr>
		<td><b>Contatos:</b></td>
		<td>Fone: ${cursoLatoForm.coordenador.telefoneContato1} - EMail: ${cursoLatoForm.coordenador.emailContato}</td>
		</tr>
		<tr>
		<td><b>Vice-Coordenador do Curso:</b></td>
		<td>${cursoLatoForm.viceCoordenador.servidor.nome}</td>
		</tr>
		<tr>
		<td><b>Contatos:</b></td>
		<td>Fone: ${cursoLatoForm.viceCoordenador.telefoneContato1} - EMail: ${cursoLatoForm.viceCoordenador.emailContato}</td>
		</tr>
		<tr>
		<td><b>Secretário:</b></td>
		<td>${cursoLatoForm.secretario.servidor.nome}</td>
		</tr>
		<tr>
		<td><b>Contatos:</b></td>
		<td>Fone: ${cursoLatoForm.secretario.telefoneContato1} - EMail: ${cursoLatoForm.secretario.emailContato}</td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td>
	<table class="subformulario" width="100%">
	<caption>Caracterização do Curso</caption>
		<tr>
		<td><b>Período de Realização:</b></td>
		<td>
		<ufrn:format name="cursoLatoForm" property="obj.dataInicio" type="data"/> a
		<ufrn:format name="cursoLatoForm" property="obj.dataFim" type="data"/>
		</td>
		</tr>
		<tr>
		<td> <b>Carga Horária:</b> </td>
		<td> ${cursoLatoForm.obj.cargaHoraria} horas/aula</td>
		</tr>
		<tr>
		<td> <b>Tipo:</b> </td>
		<td> ${cursoLatoForm.obj.tipoCursoLato.descricao} </td>
		</tr>
		<tr>
		<td> <b>Modalidade:</b> </td>
		<td> ${cursoLatoForm.obj.modalidadeEducacao.descricao} </td>
		</tr>
		<tr>
		<td> <b>Número de vagas:</b> </td>
		<td> ${cursoLatoForm.obj.numeroVagas} </td>
		</tr>
		<tr>
		<td> <b>Público Alvo:</b> </td>
		<td><br>
		<c:forEach items="${cursoLatoForm.obj.publicosAlvoCurso}" var="item">
			${item.tipoPublicoAlvo.descricao}<br>
		</c:forEach>
		</td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td>
	<table class="subformulario" width="100%">
	<caption>Objetivos e Necessidades do Curso</caption>
		<tr>
			<td> <b>Justificativa/Objetivos:</b> </td>
		</tr>
		<tr>
			<td><p> ${cursoLatoForm.obj.propostaCurso.justificativa} </p></td>
		</tr>
		<tr>
			<td> <b>Necessidade/Importância:</b> </td>
		<tr>
			<td><p> ${cursoLatoForm.obj.propostaCurso.importancia} </p></td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td>
	<table class="subformulario" width="100%">
	<caption>Estrutura e Funcionamento do Curso</caption>
		<tr>
			<td colspan="2"> <b>a) Processo Seletivo:</b> </td>
		</tr>
		<tr>
			<td><b>Período de Inscrição:</b></td>
			<td>
			<ufrn:format name="cursoLatoForm" property="obj.propostaCurso.inicioInscSelecao" type="data"/> a
			<ufrn:format name="cursoLatoForm" property="obj.propostaCurso.fimInscSelecao" type="data"/>
			</td>
		</tr>
		<tr>
			<td> <b>Requisitos:</b> </td>
		</tr>
		<tr>
			<td colspan="2"> ${cursoLatoForm.obj.propostaCurso.requisitos} </td>
		</tr>
		<tr>
			<td><b>Período de Seleção:</b></td>
			<td>
			<ufrn:format name="cursoLatoForm" property="obj.propostaCurso.inicioSelecao" type="data"/> a
			<ufrn:format name="cursoLatoForm" property="obj.propostaCurso.fimSelecao" type="data"/>
			</td>
		</tr>
		<tr>
		<td><b>Forma Adotada:</b></td>
			<td><br>
			<c:forEach items="${cursoLatoForm.obj.propostaCurso.formasSelecaoProposta}" var="item">
				${item.formaSelecao.descricao}<br>
			</c:forEach>
			</td>
		</tr>
		<tr>
			<td colspan="2"> <b>b) Processo de Avaliação do Desempenho do Aluno	no Curso:</b> </td>
		</tr>
		<tr>
		<td><b>Formas de Avaliação:</b></td>
			<td><br>
			<c:forEach items="${cursoLatoForm.obj.propostaCurso.formasAvaliacaoProposta}" var="item">
				${item.formaAvaliacao.descricao}<br>
			</c:forEach>
			</td>
		</tr>
		<tr>
			<td><b>Frequência Obrigatória:</b></td>
			<td> ${cursoLatoForm.obj.propostaCurso.freqObrigatoria} </td>
		</tr>
		<tr>
			<td><b>Conceito/Nota Mínimo(a):</b></td>
			<td> ${cursoLatoForm.obj.propostaCurso.mediaMinimaAprovacaoDesc} </td>
		</tr>
		<tr>
			<td> <b>Certificado (Emissão):</b> </td>
			<td> <c:choose><c:when test="${cursoLatoForm.obj.certificado}">Sim</c:when><c:otherwise>Não</c:otherwise> </c:choose> </td>
		</tr>
		<c:if test="${cursoLatoForm.obj.certificado}">
		<tr>
			<td> <b>Setor Responsável:</b> </td>
			<td> ${cursoLatoForm.obj.setorResponsavelCertificado}</td>
		</tr>
		</c:if>
	</table>
	</td>
</tr>
	<tr>
		<td>
			<table class="listagem" width="100%">
			<caption class="listagem">Corpo Docente</caption>
		        <thead>
		        <tr>
		        	<td>Siape</td>
			        <td>Nome</td>
			        <td>Titulação</td>
			        <td>Instituição</td>
			    </tr>
		        </thead>
		        <tbody>

		        <c:forEach items="${cursoLatoForm.obj.cursosServidores}" var="cursoServidor">
		            <tr>
		            <c:choose>
		            	<c:when test="${cursoServidor.externo}">
							<td> - </td>
		                    <td>${cursoServidor.docenteExterno.pessoa.nome}</td>
		                    <td>${cursoServidor.docenteExterno.formacao.denominacao}</td>
		                    <td>${cursoServidor.docenteExterno.instituicao.nome}</td>
		                </c:when>
		                <c:otherwise>
		                	<td>${cursoServidor.servidor.siape}</td>
		                    <td>${cursoServidor.servidor.pessoa.nome}</td>
		                    <td>${cursoServidor.servidor.formacao.denominacao}</td>
		                    <td>${ configSistema['siglaInstituicao'] }</td>
		                </c:otherwise>
		            </c:choose>
		            </tr>
		        </c:forEach>
		    </table>
		</td>
	</tr>
	<tr>
		<td>
			<table class="listagem" width="100%">
			<caption class="listagem">Disciplinas</caption>
		        <thead>
		        <tr>
		        	<td>Código</td>
			        <td>Nome</td>
			        <td>Carga Horária</td>
			    </tr>
		        </thead>
		        <tbody>

		        <c:forEach items="${cursoLatoForm.obj.disciplinas}" var="disciplina">
		            <tr>
	                	<td>${disciplina.codigo}</td>
	                    <td>${disciplina.detalhes.nome}</td>
	                    <td>${disciplina.chTotal}</td>
		            </tr>
		            <tr>
		            	<td><b>Ementa:</b></td>
		            	<td colspan="2">${disciplina.detalhes.ementa}</td>
		            </tr>
		            <tr>
		            	<td><b>Bibliografia:</b></td>
		            	<td colspan="2">${disciplina.bibliografia}</td>
		            </tr>
		        </c:forEach>
		    </table>
		</td>
	</tr>
</table>

</html:form>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>