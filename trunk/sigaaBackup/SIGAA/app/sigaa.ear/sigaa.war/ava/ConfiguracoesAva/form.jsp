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
				<legend>Configura��es da Turma Virtual</legend>
					<div class="descricaoOperacao">
						<p>Configure a maneira atrav�s da qual os alunos desta turma poder�o interagir com a Turma Virtual. 
						Voc� poder� informar se eles poder�o ou n�o cadastrar f�runs e enquetes, al�m de configurar a forma
						como eles visualizar�o as suas notas.</p>
						
						<p>
							� poss�vel tamb�m definir como a m�dia da unidade ser� calculada caso deseje-se dividir uma unidade
							em avalia��es. Para isso, selecione uma das op��es do campo <i>"No cadastro de avalia��es, a m�dia da unidade ser�:"</i>.
							As op��es poss�veis s�o: 
							
							<ul style="margin-left: 3em; margin-top: 10px;">
								<li> <b>M�dia Ponderada:</b> Cada avalia��o dever� ter um peso. A nota da unidade ser� calculada multiplicando as notas pelos pesos, somando os resultados e dividindo tudo pela soma dos pesos.</li>
								<li> <b>M�dia Aritm�tica:</b> As notas das avalia��es ser�o somadas e o resultado ser� dividido pelo n�mero de avalia��es.</li>
								<li> <b>Soma das Notas:</b> As notas das avalia��es ser�o somadas e a soma ser� a nota da unidade.</li>
							</ul>
						</p>
						
						<c:if test="${ configuracoesAva.turmaChamadaBiometrica }">
							<br/><center><b>Chamada Biom�trica</b></center>				
							
							<p>Caro professor, foi identificado pelo sistema que <b>sua turma est� habilitada a realizar a chamada atrav�s de biometria</b>. Isso possibilita que os pr�prios discentes 
								registrem sua presen�a de forma online no SIGAA usando suas respectivas digitais. 
								
								<br><br>
								<p>Para utilizar este recurso em sala de aula, � necess�rio que voc�:
								<ul style="margin-left: 3em; margin-top: 10px;">
									<li> <b>Crie uma senha num�rica de 6 a 8 d�gitos. </b>Essa senha ser� utilizada na abertura do sistema que est� instalado nos computadores em sala de aula. </li>
									<li>
										<b>Defina um tempo de toler�ncia m�nimo </b> (de 5 a 20 minutos). Esse tempo vai permitir aos alunos que chegarem atrasados dentro desse limite
										ainda assim recebam todas as presen�as da aula correspondente ao registrarem sua presen�a pela digital.
									</li>
								</ul>
								</p>
						</c:if>
							
					</div>
					
					<table class="formAva" style="width:100%;">
						<tr>
							<th style="vertical-align:top;">Qual o template para visualiza��o da turma virtual?</th>
							<td>
								<table class="itensSelecionaveis">
									<tr id="linhaTemplates"><td onclick="selecionaTemplate(1);">
										<h:graphicImage value="/ava/img/tres_colunas.png" alt="Tr�s colunas" title="Tr�s colunas" /><br/>
										Tr�s colunas
									</td><td onclick="selecionaTemplate(2);">
										<h:graphicImage value="/ava/img/duas_colunas.png" alt="Menu DropDown" title="Menu DropDown" /><br/>
										Menu DropDown
									</td></tr>
								</table>
								
								<h:selectOneMenu value="#{ configuracoesAva.config.template }" id="templateTurma" style="display:none;">
									<f:selectItem itemValue="1" itemLabel="Tr�s Colunas" />
									<f:selectItem itemValue="2" itemLabel="Menu Dropdown" />
								</h:selectOneMenu>
							</td> 
						</tr>
						<tr>
							<th style="width:50%;vertical-align:top;">Como os alunos devem visualizar as aulas?</th>
							<td>
								<table class="itensSelecionaveis">
									<tr id="linhaEstiloVisualizacaoTopicos"><td onclick="selecionaEstiloVisualizacaoTopicos(1);">
										<h:graphicImage value="/ava/img/topicos_em_lista.png" alt="T�picos em Lista" title="T�picos em Lista" /><br/>
										T�picos em Lista
									</td><td onclick="selecionaEstiloVisualizacaoTopicos(2);">
										<h:graphicImage value="/ava/img/topicos_paginados.png" alt="Um t�pico por p�gina" title="Um t�pico por p�gina" /><br/>
										Um t�pico por p�gina
									</td></tr>
								</table>
								
								<h:selectOneMenu id="estiloVisualizacaoTopicos" value="#{ configuracoesAva.config.estiloVisualizacaoTopicos }" style="display:none;">
									<f:selectItem itemLabel="Todos em Lista" itemValue="1"/>
									<f:selectItem itemLabel="Um t�pico por p�gina" itemValue="2"/>
								</h:selectOneMenu>
							</td>
						</tr>
						<c:if test="${fn:length(configuracoesAva.turma.docentesTurmas) > 1 }">
							<tr>
								<th>Mostrar a foto dos docentes selecionados no t�pico de aula?</th>
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
							<th nowrap="nowrap">No cadastro de avalia��es, a m�dia da unidade 1 ser�:</th>
							<td nowrap="nowrap">
								<h:selectOneMenu value="#{ configuracoesAva.config.tipoMediaAvaliacoes1 }" id="idMediaAvaliacao1">
								<f:selectItems value="#{ configuracoesAva.tiposMediaAvaliacoes }"/>
								</h:selectOneMenu>
							</td> 
						</tr>
						<c:if test="${configuracoesAva.numeroUnidadesTurma > 1 }">
							<tr>
								<th nowrap="nowrap">No cadastro de avalia��es, a m�dia da unidade 2 ser�:</th>
								<td nowrap="nowrap">
									<h:selectOneMenu value="#{ configuracoesAva.config.tipoMediaAvaliacoes2 }" id="idMediaAvaliacao2">
									<f:selectItems value="#{ configuracoesAva.tiposMediaAvaliacoes }"/>
									</h:selectOneMenu>
								</td> 
							</tr>
						</c:if>
						<c:if test="${configuracoesAva.numeroUnidadesTurma > 2 }">	
							<tr>
								<th nowrap="nowrap">No cadastro de avalia��es, a m�dia da unidade 3 ser�:</th>
								<td nowrap="nowrap">
									<h:selectOneMenu value="#{ configuracoesAva.config.tipoMediaAvaliacoes3 }" id="idMediaAvaliacao3">
									<f:selectItems value="#{ configuracoesAva.tiposMediaAvaliacoes }"/>
									</h:selectOneMenu>
								</td> 
							</tr>
						</c:if>	
						<tr>
							<th>Na listagem de notas, o aluno poder� ver:</th>
							<td>
								<h:selectOneMenu value="#{ configuracoesAva.config.tipoVisualizacaoNota }" id="tipoVisualizacao">
								<f:selectItems value="#{ configuracoesAva.tiposVisualizacaoNota }"/>
								</h:selectOneMenu>
							</td> 
						</tr>
						<tr>
							<th>Qual o tamanho m�ximo dos arquivos que os alunos podem enviar?</th>
							<td>
								<h:selectOneMenu value="#{ configuracoesAva.config.tamanhoUploadAluno }" id="tamanhoUploadAluno">
									<f:selectItems value="#{ configuracoesAva.tamanhosEnvioAluno }"/>
								</h:selectOneMenu> MB
							</td> 
						</tr>
						<tr>
							<th>Mostrar m�dia da turma no relat�rio de notas?</th>
							<td>
								<h:selectOneRadio value="#{ configuracoesAva.config.mostrarMediaDaTurma }" styleClass="noborder" id="mostrarMediaTurma">
								<f:selectItems value="#{ configuracoesAva.simNao }"/>
								</h:selectOneRadio>
							</td>
						</tr>
						<tr>
							<th>Mostrar relat�rio de estat�sticas de notas?</th>
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
								<th>Tempo toler�ncia:</th>
								<td>
									<input type="text" value="20" id="tempoTolerancia" name="tempoTolerancia" size="2" maxlength="2" onkeyup="formatarInteiro(this)" /> min.
								</td>
							</tr>
							
							<tr>
								<th>Senha da chamada Biom�trica:</th>
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
					Exibe o no formul�rio de configura��es somente a op��o de acesso p�blico, quando usu�rio acessar a 
					op��o Configurar > Publicar Turma
				--%>
				<c:otherwise>
				<legend>Publicar Turma Virtual</legend>
				
					<div class="descricaoOperacao">
							<%--
							<li>Os respectivos discentes, onde os mesmos ter�o acesso
								aos t�picos de aulas, notas, f�runs, enquetes, tarefas ou qualquer outra intera��o 
								docente-discente e discente-discente.
								Maiores informa��es acesse a op��o <b>Configura��es &rsaquo; Configurar Turma</b>.
							</li>--%>
							<p>Essa op��o permite ao docente tornar p�blico a turma virtual, atrav�s do portal <a href="${ ctx }/public/curso_aberto/portal.jsf?aba=p-ensino" target="_blank" title="Acessar Portal P�blico dos Cursos Abertos">
								Portal P�blico dos Cursos Abertos</a>, onde qualquer pessoa poder� visualizar <b>somente</b> os t�picos de aulas e materiais relacionados.
							</p>
							<p>
								Caso concorde, a turma virtual ser� disponibilizada neste portal para toda internet ter acesso. 
							</p>
					</div>	
				
					<table id="cursosAbertos">
						<tbody>
							<tr>
								<th>Publicar Turma Virtual ?</th>
								<td>
									<h:selectOneRadio value="#{ configuracoesAva.config.permiteVisualizacaoExterna }">
										<f:selectItem  itemLabel=" Sim " itemValue="#{true}"   />
										<f:selectItem itemLabel=" N�o " itemValue="#{false}"/>
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
						Itens de Preenchimento Obrigat�rio
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
