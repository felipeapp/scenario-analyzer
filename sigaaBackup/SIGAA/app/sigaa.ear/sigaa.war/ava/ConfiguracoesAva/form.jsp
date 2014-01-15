<%@include file="/ava/cabecalho.jsp" %>
<style>
	table#cursosAbertos{margin:auto;}
	table#cursosAbertos label{padding-right:15px;}
</style>

<f:view>
	<a4j:keepAlive beanName="configuracoesAva"></a4j:keepAlive>
	<a4j:keepAlive beanName="tarefaTurma"></a4j:keepAlive>
	<a4j:keepAlive beanName="questionarioTurma" />
	<%@include file="/ava/menu.jsp" %>
	<h:form id="form">
	
	
		<fieldset>
			
			<c:choose>
				<c:when test="${!configuracoesAva.opcaoCursosAbertos}">
				<legend>Configurações da Turma Virtual</legend>
					<div class="descricaoOperacao">
						<p>Configure a maneira através da qual os alunos desta turma poderão interagir com a Turma Virtual. 
						Você poderá informar se eles poderão ou não cadastrar fóruns e enquetes, além de configurar a forma
						como eles visualizarão as suas notas.</p>
						
						<p>
							É possível também definir como a média da unidade será calculada caso deseje-se dividir uma unidade
							em avaliações. Para isso, selecione uma das opções do campo <i>"No cadastro de avaliações, a média da unidade será:"</i>.
							As opções possíveis são: 
							
							<ul style="margin-left: 3em; margin-top: 10px;">
								<li> <b>Média Ponderada:</b> Cada avaliação deverá ter um peso. A nota da unidade será calculada multiplicando as notas pelos pesos, somando os resultados e dividindo tudo pela soma dos pesos.</li>
								<li> <b>Média Aritmética:</b> As notas das avaliações serão somadas e o resultado será dividido pelo número de avaliações.</li>
								<li> <b>Soma das Notas:</b> As notas das avaliações serão somadas e a soma será a nota da unidade.</li>
							</ul>
						</p>
						
						<c:if test="${ configuracoesAva.turmaChamadaBiometrica }">
							<br/><center><b>Chamada Biométrica</b></center>				
							
							<p>Caro professor, foi identificado pelo sistema que <b>sua turma está habilitada a realizar a chamada através de biometria</b>. Isso possibilita que os próprios discentes 
								registrem sua presença de forma online no SIGAA usando suas respectivas digitais. 
								
								<br><br>
								<p>Para utilizar este recurso em sala de aula, é necessário que você:
								<ul style="margin-left: 3em; margin-top: 10px;">
									<li> <b>Crie uma senha numérica de 6 a 8 dígitos. </b>Essa senha será utilizada na abertura do sistema que está instalado nos computadores em sala de aula. </li>
									<li>
										<b>Defina um tempo de tolerância mínimo </b> (de 5 a 20 minutos). Esse tempo vai permitir aos alunos que chegarem atrasados dentro desse limite
										ainda assim recebam todas as presenças da aula correspondente ao registrarem sua presença pela digital.
									</li>
								</ul>
								</p>
						</c:if>
							
					</div>
					
					<table class="formAva" style="width:100%;">
						<tr>
							<th style="vertical-align:top;">Qual o template para visualização da turma virtual?</th>
							<td>
								<table class="itensSelecionaveis">
									<tr id="linhaTemplates"><td onclick="selecionaTemplate(1);">
										<h:graphicImage value="/ava/img/tres_colunas.png" alt="Três colunas" title="Três colunas" /><br/>
										Três colunas
									</td><td onclick="selecionaTemplate(2);">
										<h:graphicImage value="/ava/img/duas_colunas.png" alt="Menu DropDown" title="Menu DropDown" /><br/>
										Menu DropDown
									</td></tr>
								</table>
								
								<h:selectOneMenu value="#{ configuracoesAva.config.template }" id="templateTurma" style="display:none;">
									<f:selectItem itemValue="1" itemLabel="Três Colunas" />
									<f:selectItem itemValue="2" itemLabel="Menu Dropdown" />
								</h:selectOneMenu>
							</td> 
						</tr>
						<tr>
							<th style="width:50%;vertical-align:top;">Como os alunos devem visualizar as aulas?</th>
							<td>
								<table class="itensSelecionaveis">
									<tr id="linhaEstiloVisualizacaoTopicos"><td onclick="selecionaEstiloVisualizacaoTopicos(1);">
										<h:graphicImage value="/ava/img/topicos_em_lista.png" alt="Tópicos em Lista" title="Tópicos em Lista" /><br/>
										Tópicos em Lista
									</td><td onclick="selecionaEstiloVisualizacaoTopicos(2);">
										<h:graphicImage value="/ava/img/topicos_paginados.png" alt="Um tópico por página" title="Um tópico por página" /><br/>
										Um tópico por página
									</td></tr>
								</table>
								
								<h:selectOneMenu id="estiloVisualizacaoTopicos" value="#{ configuracoesAva.config.estiloVisualizacaoTopicos }" style="display:none;">
									<f:selectItem itemLabel="Todos em Lista" itemValue="1"/>
									<f:selectItem itemLabel="Um tópico por página" itemValue="2"/>
								</h:selectOneMenu>
							</td>
						</tr>
						<c:if test="${fn:length(configuracoesAva.turma.docentesTurmas) > 1 }">
							<tr>
								<th>Mostrar a foto dos docentes selecionados no tópico de aula?</th>
								<td>
									<h:selectOneRadio value="#{ configuracoesAva.config.mostrarFotoTopico }" styleClass="noborder">
										<f:selectItems value="#{ configuracoesAva.simNao }"/>
									</h:selectOneRadio>
								</td>
							</tr>
						</c:if>
						<tr>
							<th>Alunos podem criar enquetes?</th>
							<td>
								<h:selectOneRadio value="#{ configuracoesAva.config.permiteAlunoCriarEnquete }" styleClass="noborder">
								<f:selectItems value="#{ configuracoesAva.simNao }"/>
								</h:selectOneRadio>
							</td>
						</tr>
						<tr>
							<th>Alunos podem alterar o nome dos grupos?</th>
							<td>
								<h:selectOneRadio value="#{ configuracoesAva.config.permiteAlunoModificarNomeGrupo }" styleClass="noborder">
								<f:selectItems value="#{ configuracoesAva.simNao }"/>
								</h:selectOneRadio>
							</td>
						</tr>
						<tr>
							<th>Publicar no Portal dos Cursos Abertos?</th>
							<td>
								<h:selectOneRadio value="#{ configuracoesAva.config.permiteVisualizacaoExterna }" styleClass="noborder">
								<f:selectItems value="#{ configuracoesAva.simNao }"/>
								</h:selectOneRadio>
							</td>
						</tr>
						<tr>
							<th nowrap="nowrap">No cadastro de avaliações, a média da unidade 1 será:</th>
							<td nowrap="nowrap">
								<h:selectOneMenu value="#{ configuracoesAva.config.tipoMediaAvaliacoes1 }" id="idMediaAvaliacao1">
								<f:selectItems value="#{ configuracoesAva.tiposMediaAvaliacoes }"/>
								</h:selectOneMenu>
							</td> 
						</tr>
						<c:if test="${configuracoesAva.numeroUnidadesTurma > 1 }">
							<tr>
								<th nowrap="nowrap">No cadastro de avaliações, a média da unidade 2 será:</th>
								<td nowrap="nowrap">
									<h:selectOneMenu value="#{ configuracoesAva.config.tipoMediaAvaliacoes2 }" id="idMediaAvaliacao2">
									<f:selectItems value="#{ configuracoesAva.tiposMediaAvaliacoes }"/>
									</h:selectOneMenu>
								</td> 
							</tr>
						</c:if>
						<c:if test="${configuracoesAva.numeroUnidadesTurma > 2 }">	
							<tr>
								<th nowrap="nowrap">No cadastro de avaliações, a média da unidade 3 será:</th>
								<td nowrap="nowrap">
									<h:selectOneMenu value="#{ configuracoesAva.config.tipoMediaAvaliacoes3 }" id="idMediaAvaliacao3">
									<f:selectItems value="#{ configuracoesAva.tiposMediaAvaliacoes }"/>
									</h:selectOneMenu>
								</td> 
							</tr>
						</c:if>	
						<tr>
							<th>Na listagem de notas, o aluno poderá ver:</th>
							<td>
								<h:selectOneMenu value="#{ configuracoesAva.config.tipoVisualizacaoNota }" id="tipoVisualizacao">
								<f:selectItems value="#{ configuracoesAva.tiposVisualizacaoNota }"/>
								</h:selectOneMenu>
							</td> 
						</tr>
						<tr>
							<th>Qual o tamanho máximo dos arquivos que os alunos podem enviar?</th>
							<td>
								<h:selectOneMenu value="#{ configuracoesAva.config.tamanhoUploadAluno }" id="tamanhoUploadAluno">
									<f:selectItems value="#{ configuracoesAva.tamanhosEnvioAluno }"/>
								</h:selectOneMenu> MB
							</td> 
						</tr>
						<tr>
							<th>Mostrar média da turma no relatório de notas?</th>
							<td>
								<h:selectOneRadio value="#{ configuracoesAva.config.mostrarMediaDaTurma }" styleClass="noborder" id="mostrarMediaTurma">
								<f:selectItems value="#{ configuracoesAva.simNao }"/>
								</h:selectOneRadio>
							</td>
						</tr>
						<tr>
							<th>Mostrar relatório de estatísticas de notas?</th>
							<td>
								<h:selectOneRadio value="#{ configuracoesAva.config.mostrarEstatisticaNotas }" styleClass="noborder" id="mostrarEstatisticaNotas">
								<f:selectItems value="#{ configuracoesAva.simNao }"/>
								</h:selectOneRadio>
							</td>
						</tr>
						<tr>
							<th>Data de fim da 1<sup>a</sup> unidade:</th>
							<td style="position:relative;display:block;">
								<t:inputCalendar style="z-index:999;" popupButtonStyle="z-index:0;" id="data1" value="#{configuracoesAva.config.dataFimPrimeiraUnidade}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return (formataData(this,event));"  maxlength="10" title="Data 1">
									<f:convertDateTime pattern="dd/MM/yyyy"/>
								</t:inputCalendar>
							</td>
						</tr>
						<c:if test="${configuracoesAva.numeroUnidadesTurma > 1 }">
							<tr>
								<th>Data de fim da 2<sup>a</sup> unidade:</th>
								<td style="position:relative;display:block;">
									<t:inputCalendar style="z-index:999;" popupButtonStyle="z-index:0;" id="data2" value="#{configuracoesAva.config.dataFimSegundaUnidade }" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return (formataData(this,event));"  maxlength="10" title="Data 2">
										<f:convertDateTime pattern="dd/MM/yyyy"/>
									</t:inputCalendar>
								</td>
							</tr>
						</c:if>
						<c:if test="${configuracoesAva.numeroUnidadesTurma > 2 }">
							<tr>
								<th>Data de fim da 3<sup>a</sup> unidade:</th>
								<td style="position:relative;display:block;">
									<t:inputCalendar style="z-index:999;" popupButtonStyle="z-index:0;" id="data3" value="#{configuracoesAva.config.dataFimTerceiraUnidade }" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return (formataData(this,event));"  maxlength="10" title="Data 3">
										<f:convertDateTime pattern="dd/MM/yyyy"/>
									</t:inputCalendar>
								</td>
							</tr>
						</c:if>
						<tr>
							<th>Ocultar as notas dos alunos:</th>
							<td>
								<h:selectOneRadio value="#{ configuracoesAva.config.ocultarNotas }" id="ocultarNota">
									<f:selectItems value="#{ configuracoesAva.simNao }"/>
								</h:selectOneRadio>
							</td> 
						</tr>
						<c:if test="${ configuracoesAva.turmaChamadaBiometrica }">
							<tr>
								<th>Tempo tolerância:</th>
								<td>
									<input type="text" value="20" id="tempoTolerancia" name="tempoTolerancia" size="2" maxlength="2" onkeyup="formatarInteiro(this)" /> min.
								</td>
							</tr>
							
							<tr>
								<th>Senha da chamada Biométrica:</th>
								<td>
									<input type="password" id="senha1" name="senha1" size="10" maxlength="8" onkeyup="formatarInteiro(this)" /> 
								</td>
							</tr>
							
							<tr>
								<th>Repita a senha:</th>
								<td>
									<input type="password" id="senha2" name="senha2" size="10" maxlength="8" onkeyup="formatarInteiro(this)" />
								</td>
							</tr>
						</c:if>
						
					</table>
				</c:when>
				<%--
					Exibe o no formulário de configurações somente a opção de acesso público, quando usuário acessar a 
					opção Configurar > Publicar Turma
				--%>
				<c:otherwise>
				<legend>Publicar Turma Virtual</legend>
				
					<div class="descricaoOperacao">
							<%--
							<li>Os respectivos discentes, onde os mesmos terão acesso
								aos tópicos de aulas, notas, fóruns, enquetes, tarefas ou qualquer outra interação 
								docente-discente e discente-discente.
								Maiores informações acesse a opção <b>Configurações &rsaquo; Configurar Turma</b>.
							</li>--%>
							<p>Essa opção permite ao docente tornar público a turma virtual, através do portal <a href="${ ctx }/public/curso_aberto/portal.jsf?aba=p-ensino" target="_blank" title="Acessar Portal Pùblico dos Cursos Abertos">
								Portal Público dos Cursos Abertos</a>, onde qualquer pessoa poderá visualizar <b>somente</b> os tópicos de aulas e materiais relacionados.
							</p>
							<p>
								Caso concorde, a turma virtual será disponibilizada neste portal para toda internet ter acesso. 
							</p>
					</div>	
				
					<table id="cursosAbertos">
						<tbody>
							<tr>
								<th>Publicar Turma Virtual ?</th>
								<td>
									<h:selectOneRadio value="#{ configuracoesAva.config.permiteVisualizacaoExterna }">
										<f:selectItem  itemLabel=" Sim " itemValue="#{true}"   />
										<f:selectItem itemLabel=" Não " itemValue="#{false}"/>
									</h:selectOneRadio>
									<h:inputHidden value="#{ configuracoesAva.opcaoCursosAbertos}"/>
								</td>
							</tr>
						</tbody>			
					</table>	
				</c:otherwise>	
			</c:choose>	
		
			<h:inputHidden value="#{ configuracoesAva.config.id }"/>
			<h:inputHidden value="#{ configuracoesAva.cadastroAvaliacao }"/>
		
			<div class="botoes">
				<div class="form-actions">
					<h:commandButton action="#{configuracoesAva.salvar}" value="Salvar" />
				</div>
				
				<div class="other-actions">
					<h:commandButton action="#{configuracoesAva.cancelar}" value="Cancelar" onclick="#{confirm}" />
				</div>
				
				<%--
					<div class="required-items">
						<span class="required">&nbsp;</span>
						Itens de Preenchimento Obrigatório
					</div>
				--%>
			</div>
	
		</fieldset>
	
	</h:form>
	
	
	<script>
		function selecionaTemplate (valor){
			document.getElementById('form:templateTurma').value = valor;
	
			var f = document.getElementById('linhaTemplates').firstChild;
			f.className = valor == 1 ? 'itemSelecionado' : '';
			f.nextSibling.className = valor == 2 ? 'itemSelecionado' : '';
		}
	
		selecionaTemplate (<h:outputText value="#{ configuracoesAva.config.template }" />);
		
		function selecionaEstiloVisualizacaoTopicos (valor){
			document.getElementById('form:estiloVisualizacaoTopicos').value = valor;
	
			var f = document.getElementById('linhaEstiloVisualizacaoTopicos').firstChild;
			f.className = valor == 1 ? 'itemSelecionado' : '';
			f.nextSibling.className = valor == 2 ? 'itemSelecionado' : '';
		}
		
		selecionaEstiloVisualizacaoTopicos (<h:outputText value="#{ configuracoesAva.config.estiloVisualizacaoTopicos }" />);
		
	</script>

</f:view>
<%@include file="/ava/rodape.jsp" %>
