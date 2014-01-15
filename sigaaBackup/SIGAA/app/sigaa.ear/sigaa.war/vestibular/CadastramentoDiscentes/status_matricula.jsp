<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastramento de Discentes Convocados</h2>

	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<p>Selecione o status que o discente ter� ao fim do cadastramento:
		<ul>
			<li><b>CADASTRADO:</b> O discente cumpriu todas as exig�ncias para ter v�nculo com a institui��o.</li>
			<li><b>PR�-CADASTRO:</b> O discente apresenta alguma pend�ncia que o impede neste momento de ter o cadastro completo.</li>
		</ul>
		</p> 
	</div>
	<br/>
	<h:form id="form">
		<table class="visualizacao" >
			<caption>Dados do Discente</caption>
			<tr>
				<th>Processo Seletivo:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.convocacaoProcessoSeletivo.processoSeletivo.nome }</td>
			</tr>
			<tr>
				<th>Convoca��o:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.convocacaoProcessoSeletivo.descricao }</td>
			</tr>
			<tr>
				<th>Situa��o do Candidato:</th>
				<td>
					<h:outputText value="Candidato APROVADO" rendered="#{ cadastramentoDiscenteConvocadoMBean.obj.dentroNumeroVagas }" />
					<h:outputText value="Candidato CLASSIFICADO" rendered="#{not cadastramentoDiscenteConvocadoMBean.obj.dentroNumeroVagas }" />
				</td>
			</tr>
			<tr>
				<th>CPF:</th>
				<td>
					<ufrn:format type="cpf" valor="${ cadastramentoDiscenteConvocadoMBean.obj.inscricaoVestibular.pessoa.cpf_cnpj }"></ufrn:format>
				</td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.discente.pessoa.nome }</td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>${ cadastramentoDiscenteConvocadoMBean.obj.matrizCurricular.descricao }</td>
			</tr>
		</table>
		<br/>
		<table class="formulario" width="99%">
			<caption class="listagem">Informa��es Complementares do Discente</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Status:</th>
					<td>
						<h:selectOneMenu id="status" onchange="submit()"
							value="#{cadastramentoDiscenteConvocadoMBean.obj.discente.status}">
							<f:selectItems value="#{cadastramentoDiscenteConvocadoMBean.statusDiscentesCombo}" />
						</h:selectOneMenu>
						<ufrn:help>Defina o status que o discente ter� ao final do cadastramento.
							O status <b>"CADASTRADO"</b> � quando o candidato est� classificado dentro do n�mero de vagas. Caso contr�rio,
							o status do discente dever� ser <b>"PR� CADASTRADO"</b>. 
						</ufrn:help>
					</td>
				</tr>
				<tr>
					<th class="${ cadastramentoDiscenteConvocadoMBean.obj.discente.discente.preCadastro 
								  || empty cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressanteCombo ? 'rotulo' : '' }"
						valign="top">Plano de Matr�cula:</th>
					<td valign="top">
						<h:selectOneRadio id="planoMatricula" layout="pageDirection" onchange="submit()" onclick="submit()"
							valueChangeListener="#{cadastramentoDiscenteConvocadoMBean.carregaDadosPlanoMatricula }"
							rendered="#{ not cadastramentoDiscenteConvocadoMBean.obj.discente.discente.preCadastro 
										 && not empty cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressanteCombo}"
							value="#{cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressante.id}">
							<f:selectItems value="#{cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressanteCombo}" />
							<f:selectItem itemValue="0" itemLabel="N�O MATRICULAR O DISCENTE EM TURMAS" id="naoMatricula"/>
						</h:selectOneRadio>
						<h:outputText value="Somente discentes cadastrados poder�o efetuar matr�cula." rendered="#{ cadastramentoDiscenteConvocadoMBean.obj.discente.discente.preCadastro }" />
						<h:outputText value="N�o h� plano de matr�cula para a Matriz Curricular do discente." rendered="#{ not cadastramentoDiscenteConvocadoMBean.obj.discente.discente.preCadastro && empty cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressanteCombo }" />
					</td>
				</tr>
				<c:if test="${ !cadastramentoDiscenteConvocadoMBean.obj.discente.discente.preCadastro  && cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressante.id > 0}">
					<tr>
						<td class="subFormulario" colspan="2">Turmas do Plano de Matr�cula</td>
					</tr>
					<tr>
						<td colspan="2">
							<a4j:outputPanel id="dadosPlanoMatricula" >
								<table class="listagem" width="100%">
									<thead>
									<tr>
										<th width="8%">C�digo</th>
										<th>Disciplina</th>
										<th>Turma</th>
										<th>Hor�rio</th>
									</tr>
									</thead>
									<tbody>
										<c:forEach items="#{ cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressante.turmas }" var="item">
										<tr>
											<td>${ item.disciplina.codigo }</td>
											<td>${ item.disciplina.nome }</td>
											<td>${ item.codigo }</td>
											<td>${ item.descricaoHorario }</td>
										</tr>
										</c:forEach>
									</tbody>
								</table>
							</a4j:outputPanel>
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="<< Voltar" action="#{ cadastramentoDiscenteConvocadoMBean.formDadosPessoais }" id="voltar"/>
						<h:commandButton value="Cancelar" action="#{ cadastramentoDiscenteConvocadoMBean.cancelar }" id="cancelar" onclick="#{ confirm }"/>
						<h:commandButton value="Avan�ar >>" action="#{ cadastramentoDiscenteConvocadoMBean.confirmar }" id="submeterDocumentacao"
							onclick="if ($('form:planoMatricula:#{ fn:length(cadastramentoDiscenteConvocadoMBean.planoMatriculaIngressanteCombo) }').checked) {return confirm('ATEN��O! O discente n�o ser� matriculado em turmas. Deseja continuar?');} else {return true;}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br />
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>