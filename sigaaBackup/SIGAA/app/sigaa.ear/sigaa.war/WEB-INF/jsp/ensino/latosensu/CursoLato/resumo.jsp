<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>
<ufrn:steps/>
</h2>

<html:form action="/ensino/latosensu/criarCurso" method="post">
	<html:hidden property="id" value="${ cursoLatoForm.obj.id }"/>
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
		<tr class="linhaImpar">
		<td><b>Grande �rea e �rea do Conhecimento:</b></td>
		<td>${cursoLatoForm.obj.areaConhecimentoCnpq.grandeArea.nome} - ${cursoLatoForm.obj.areaConhecimentoCnpq.subarea.nome}</td>
		</tr>
		<tr class="linhaImpar">
		<td><b>Unidade Respons�vel:</b></td>
		<td>${cursoLatoForm.obj.unidade.nome}</td>
		</tr>
		<tr>
		<td><b>Coordenador do Curso:</b></td>
		<td>${cursoLatoForm.coordenador.servidor.nome}</td>
		</tr>
		<tr>
		<td><b>Contato:</b></td>
		<td>Fone: ${cursoLatoForm.coordenador.telefoneContato1} - EMail: ${cursoLatoForm.coordenador.emailContato}</td>
		</tr>
		<tr class="linhaImpar">
		<td><b>Vice-Coordenador do Curso:</b></td>
		<td>${cursoLatoForm.viceCoordenador.servidor.nome}</td>
		</tr>
		<tr class="linhaImpar">
		<td><b>Contato:</b></td>
		<td>Fone: ${cursoLatoForm.viceCoordenador.telefoneContato1} - EMail: ${cursoLatoForm.viceCoordenador.emailContato}</td>
		</tr>
		<tr>
		<td><b>Secret�rio:</b></td>
		<td>${cursoLatoForm.secretario.servidor.nome}</td>
		</tr>
		<tr>
		<td><b>Contato:</b></td>
		<td>Fone: ${cursoLatoForm.secretario.telefoneContato1} - EMail: ${cursoLatoForm.secretario.emailContato}</td>
		</tr>
		<tr>
		<td><b>Portaria:</b></td>
		<td>N�mero Processo: ${cursoLatoForm.obj.propostaCurso.numeroProcesso} </td>
		</tr>
		<tr>
		<td></td>
		<td>Data P�blica��o: ${cursoLatoForm.obj.propostaCurso.dataPublicacaoPortaria} </td>
		</tr>
		<tr>
		<td></td>
		<td>Ano Processo: ${cursoLatoForm.obj.propostaCurso.anoProcesso} </td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td>
	<table class="subformulario" width="100%">
	<caption>Caracteriza��o do Curso</caption>
		<tr>
		<td><b>Per�odo de Realiza��o:</b>
		<ufrn:format name="cursoLatoForm" property="obj.dataInicio" type="data"/> a
		<ufrn:format name="cursoLatoForm" property="obj.dataFim" type="data"/>
		</td>
		</tr>
		<tr>
		<td> <b>Carga Hor�ria:</b> ${cursoLatoForm.obj.cargaHoraria} horas/aula </td>
		</tr>
		<tr>
		<td> <b>Tipo:</b> ${cursoLatoForm.obj.tipoCursoLato.descricao} </td>
		</tr>
		<tr>
		<td> <b>Modalidade:</b> ${cursoLatoForm.obj.modalidadeEducacao.descricao} </td>
		</tr>
		<tr>
		<td> <b>N�mero de vagas:</b> ${cursoLatoForm.obj.numeroVagas} </td>
		</tr>
		<tr>
		<td> <b>P�blico Alvo:</b> <br>
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
			<td> <b>Justificativa/Objetivos:</b> <br>
			 <ufrn:format type="texto" valor="${cursoLatoForm.obj.propostaCurso.justificativa}"/> </td>
		</tr>
		<tr>
			<td> <b>Necessidade/Import�ncia:</b> <br>
			<ufrn:format type="texto" valor="${cursoLatoForm.obj.propostaCurso.importancia}"/> </td>
		</tr>
	</table>
	</td>
</tr>
<tr>
	<td>
	<table class="subformulario" width="100%">
	<caption>Estrutura e Funcionamento do Curso</caption>
		<tr>
			<td> <b>a) Processo Seletivo:</b> </td>
		</tr>
		<tr>
			<td><b>Per�odo de Inscri��o:</b>
			<ufrn:format name="cursoLatoForm" property="obj.propostaCurso.inicioInscSelecao" type="data"/> a
			<ufrn:format name="cursoLatoForm" property="obj.propostaCurso.fimInscSelecao" type="data"/>
			</td>
		</tr>
		<tr>
			<td> <b>Requisitos:</b> <br>
			${cursoLatoForm.obj.propostaCurso.requisitos} </td>
		</tr>
		<tr>
			<td><b>Per�odo de Sele��o:</b>
			<ufrn:format name="cursoLatoForm" property="obj.propostaCurso.inicioSelecao" type="data"/> a
			<ufrn:format name="cursoLatoForm" property="obj.propostaCurso.fimSelecao" type="data"/>
			</td>
		</tr>
		<tr>
			<td><b>Forma Adotada:</b><br>
			<c:forEach items="${cursoLatoForm.obj.propostaCurso.formasSelecaoProposta}" var="item">
				${item.formaSelecao.descricao}<br>
			</c:forEach>
			</td>
		</tr>
		<tr>
			<td> <b>b) Processo de Avalia��o do Desempenho do Aluno	no Curso:</b> </td>
		</tr>
		<tr>
			<td><b>Formas de Avalia��o:</b><br>
			<c:forEach items="${cursoLatoForm.obj.propostaCurso.formasAvaliacaoProposta}" var="item">
				${item.formaAvaliacao.descricao}<br>
			</c:forEach>
			</td>
		</tr>
		<tr>
			<td><b>Frequ�ncia Obrigat�ria:</b> ${cursoLatoForm.obj.propostaCurso.freqObrigatoria} %</td>
		</tr>
		<tr>
			<td><b>Conceito/Nota M�nimo(a):</b> ${cursoLatoForm.obj.propostaCurso.mediaMinimaAprovacaoDesc} </td>
		</tr>
		<tr>
			<td> <b>Certificado (Emiss�o):</b> <c:choose><c:when test="${cursoLatoForm.obj.certificado}">Sim</c:when><c:otherwise>N�o</c:otherwise> </c:choose> </td>
		</tr>
		<tr>
			<td> <b>Setor Respons�vel:</b> ${cursoLatoForm.obj.setorResponsavelCertificado}</td>
		</tr>
	</table>
	</td>
</tr>
	<tr>
		<td>
			<table class="listagem" width="100%">
			<caption class="listagem">Corpo Docente</caption>
		        <thead>
		        <tr>
		        	<td style="text-align: right;">Siape</td>
			        <td>Nome</td>
			        <td>Titula��o</td>
			        <td>Institui��o</td>
			    </tr>
		        </thead>
		        <tbody>

		        <c:forEach items="${cursoLatoForm.obj.cursosServidores}" var="cursoServidor" varStatus="status">
		            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
		            <c:choose>
		            	<c:when test="${cursoServidor.externo}">
							<td> - </td>
		                    <td>${cursoServidor.docenteExterno.pessoa.nome}</td>
		                    <td>${cursoServidor.docenteExterno.formacao.denominacao}</td>
		                    <td>${cursoServidor.docenteExterno.instituicao.sigla}</td>
		                </c:when>
		                <c:otherwise>
		                	<td style="text-align: right;">${cursoServidor.servidor.siape}</td>
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
		        		<td style="text-align: center;">C�digo</td>
			        	<td>Nome</td>
			        	<td style="text-align: right;">Carga Hor�ria</td>
			    	</tr>
		        </thead>
		        <tbody>
		       		<c:forEach items="${cursoLatoForm.obj.disciplinas}" var="disciplina" varStatus="status">
		            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
	                	<td style="text-align: center;">${disciplina.codigo}</td>
	                    <td>${disciplina.detalhes.nome}</td>
	                    <td style="text-align: right;">${disciplina.chTotal}</td>
		            </tr>
		            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
		            	<td>Ementa:</td>
		            	<td colspan="2">${disciplina.detalhes.ementa}</td>
		            </tr>
		            <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
		            	<td>Bibliografia:</td>
		            	<td colspan="2">${disciplina.bibliografia}</td>
		            </tr>
		        	</c:forEach>
		    </table>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="4" align="center">
				<c:if test="${param.dispatch != 'view'}">
					<html:button dispatch="chamaModelo" value="Confirmar"/>&nbsp;&nbsp;
		    		<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
		    		<c:if test="${param.dispatch != 'remove'}">
		    			&nbsp;&nbsp;<html:button view="coordenacaoCurso" value="<< Voltar"/>
		    		</c:if>
		    	</c:if>
		    	<c:if test="${param.dispatch == 'view'}">
		    		<html:button dispatch="resumoImpressao" value="Vers�o para impress�o"/>
		    		<html:button value="<< Voltar" onclick="history.go(-1);" />
		    	</c:if>
    		</td>
    	</tr>
    </tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
