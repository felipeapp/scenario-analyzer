<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<html:form action="/ensino/latosensu/criarCurso" method="post">

<table class="formulario">
<caption> Proposta de Cria��o de Curso Lato Sensu </caption>
<tr>
	<td>
	<table class="subFormulario" width="100%">
	<caption>Identifica��o do Projeto</caption>
		<tr>
		<td><b>Institui��o:</b></td>
		<td>${ configSistema['nomeInstituicao'] } - ${ configSistema['siglaInstituicao'] }</td>
		</tr>
		<tr>
		<td><b>Curso:</b></td>
		<td>${cursoLatoForm.obj.nome}</td>
		</tr>
		<tr>
		<td><b>Grande �rea e �rea do Conhecimento:</b></td>
		<td>${cursoLatoForm.obj.areaConhecimentoCnpq.grandeArea.nome}</td>
		</tr>
		<tr>
		<td><b>Unidade Respons�vel:</b></td>
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
		<td><b>Secret�rio:</b></td>
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
	<caption>Caracteriza��o do Curso</caption>
		<tr>
		<td><b>Per�odo de Realiza��o:</b></td>
		<td>
		<ufrn:format name="cursoLatoForm" property="obj.dataInicio" type="data"/> a
		<ufrn:format name="cursoLatoForm" property="obj.dataFim" type="data"/>
		</td>
		</tr>
		<tr>
		<td> <b>Carga Hor�ria:</b> </td>
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
		<td> <b>N�mero de vagas:</b> </td>
		<td> ${cursoLatoForm.obj.numeroVagas} </td>
		</tr>
		<tr>
		<td> <b>P�blico Alvo:</b> </td>
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
			<td> <b>Necessidade/Import�ncia:</b> </td>
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
			<td><b>Per�odo de Inscri��o:</b></td>
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
			<td><b>Per�odo de Sele��o:</b></td>
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
			<td colspan="2"> <b>b) Processo de Avalia��o do Desempenho do Aluno	no Curso:</b> </td>
		</tr>
		<tr>
		<td><b>Formas de Avalia��o:</b></td>
			<td><br>
			<c:forEach items="${cursoLatoForm.obj.propostaCurso.formasAvaliacaoProposta}" var="item">
				${item.formaAvaliacao.descricao}<br>
			</c:forEach>
			</td>
		</tr>
		<tr>
			<td><b>Frequ�ncia Obrigat�ria:</b></td>
			<td> ${cursoLatoForm.obj.propostaCurso.freqObrigatoria} </td>
		</tr>
		<tr>
			<td><b>Conceito/Nota M�nimo(a):</b></td>
			<td> ${cursoLatoForm.obj.propostaCurso.mediaMinimaAprovacaoDesc} </td>
		</tr>
		<tr>
			<td> <b>Certificado (Emiss�o):</b> </td>
			<td> <c:choose><c:when test="${cursoLatoForm.obj.certificado}">Sim</c:when><c:otherwise>N�o</c:otherwise> </c:choose> </td>
		</tr>
		<c:if test="${cursoLatoForm.obj.certificado}">
		<tr>
			<td> <b>Setor Respons�vel:</b> </td>
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
			        <td>Titula��o</td>
			        <td>Institui��o</td>
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
		        	<td>C�digo</td>
			        <td>Nome</td>
			        <td>Carga Hor�ria</td>
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