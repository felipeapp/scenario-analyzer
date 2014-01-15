<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2 class="title"> <ufrn:subSistema /> > Parâmetros do Programa</h2>

	<h:form>
	
		<h:inputHidden value="#{parametrosProgramaPosBean.obj.id}" id="bancaPOSId"/>
		<h:inputHidden value="#{parametrosProgramaPosBean.obj.programa.id}" id="IDPrograma"/>
		<h:inputHidden value="#{parametrosProgramaPosBean.obj.programa.nome}" id="nomeProgramPos"/>
	
		<h:outputText value="#{parametrosProgramaPosBean.create}"></h:outputText>
		<table class="formulario" width="100%">
			<caption>Parâmetros do Programa</caption>
			<tr>
				<td colspan="2" style="text-align: center;">
					<b><h:outputText value="#{parametrosProgramaPosBean.obj.programa.nome}" /></b>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Matrícula de alunos regulares</td>
			</tr>
			<tr>
				<th></th>
				<td >
					<table width="120px;">
						<tr>
							<td width="50%"> Doutorado</td>
							<td width="50%"> Mestrado</td>
						</tr>
					</table> 
				
			</tr>

			<tr>
				<th class="required"   width="50%">Máximo de renovações da qualificação:</th>
				
				<td >
					<table width="120px;">
						<tr >
							<td width="50%" style="text-align: center;"> 
								<h:inputText size="2" value="#{parametrosProgramaPosBean.obj.maxRenovacaoQualificacaoDoutorado}" id="maxRenovacaoQualificacaoDoutorado" onkeyup="formatarInteiro(this)"/>
							</td>
							
							<td width="50%" style="text-align: center;">
								<h:inputText size="2" value="#{parametrosProgramaPosBean.obj.maxRenovacaoQualificacaoMestrado}" id="maxRenovacaoQualificacaoMestrado" onkeyup="formatarInteiro(this)"/>
							</td>
						</tr>
					</table> 
				</td>
				
			</tr>
			<tr>
				<th class="required" >Máximo de renovações da defesa:</th>
				<td >
					<table width="120px;">
						<tr >
							<td width="50%" style="text-align: center;"> 
								<h:inputText size="2" value="#{parametrosProgramaPosBean.obj.maxRenovacaoDefesaDoutorado}" id="maxRenovacaoDefesaDoutorado" onkeyup="formatarInteiro(this)"/>
							</td>
							
							<td width="50%" style="text-align: center;"> 
								<h:inputText size="2" value="#{parametrosProgramaPosBean.obj.maxRenovacaoDefesaMestrado}" id="maxRenovacaoDefesaMestrado" onkeyup="formatarInteiro(this)"/>
							</td>
						</tr>
					</table> 
				</td>
				
				
			</tr>
			
			<tr>
				<th class="required">Permite matrícula em defesa sem ter qualificação consolidada:</th>
				<td colspan="2" >
				<t:selectOneRadio  id="permiteMatricularDefesaQualificacao" value="#{parametrosProgramaPosBean.obj.permiteMatricularDefesaQualificacao}" styleClass="noborder">
					<f:selectItem itemLabel="Não" itemValue="false"  />
					<f:selectItem itemLabel="Sim" itemValue="true"  />
				</t:selectOneRadio>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Matrícula de alunos especiais</td>
			</tr>
			<tr>
				<th class="required">Permite matrícula online de alunos especiais?</th>
				<td >
				<t:selectOneRadio id="permiteMatriculaOnlineAlunosEspeciais" value="#{parametrosProgramaPosBean.obj.permiteMatriculaOnlineEspeciais}" styleClass="noborder">
					<f:selectItem itemLabel="Não" itemValue="false"/>
					<f:selectItem itemLabel="Sim" itemValue="true"/>
				</t:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th class="required">Máximo de matrículas por período para alunos especiais:</th>
				
				<td >
					<table width="60px;">
						<tr >
							<td> 
								<h:inputText size="2" value="#{parametrosProgramaPosBean.obj.maxDisciplinasAlunoEspecial}" id="maxDisciplinasAlunoEspecial" onkeyup="formatarInteiro(this)"/>
							</td>
						</tr>
					</table> 
				</td>
				
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Processos seletivos</td>
			</tr>
			<tr>
				<th class="required">Solicitar área e linha de pesquisa na inscrição da seleção:</th>
				<td colspan="2">
				<t:selectOneRadio id="solicitarAreaLinhaProcessoSeletivo" value="#{parametrosProgramaPosBean.obj.solicitarAreaLinhaProcessoSeletivo}" styleClass="noborder">
					<f:selectItem itemLabel="Não" itemValue="false"/>
					<f:selectItem itemLabel="Sim" itemValue="true"/>
				</t:selectOneRadio>
				</td>
			</tr>

			<tr>
				<th class="required">Solicitar orientador na inscrição da seleção:</th>
				<td>
					<table>
						<tr>
							<td>
							<t:selectOneRadio id="solicitarOrientadorProcessoSeletivo" value="#{parametrosProgramaPosBean.obj.solicitarOrientadorProcessoSeletivo}" styleClass="noborder">
								<f:selectItem itemLabel="Não" itemValue="false"/>
								<f:selectItem itemLabel="Sim" itemValue="true"/>
								</t:selectOneRadio>
							</td>
							<td>
								<ufrn:help>
									Selecionado SIM, irá aparecer um campo para o discente informar um Orientador na inscrição do Processe Seletivo, mas o aluno não é obrigado a informar e pode simplesmente deixar este campo em branco.
								</ufrn:help>	
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<th class="required">Solicitar o arquivo de um projeto (em formato PDF):</th>
				<td >
				<t:selectOneRadio id="solicitarArquivo" value="#{parametrosProgramaPosBean.obj.solicitaProjetoNaInscricao}" styleClass="noborder">
					<f:selectItem itemLabel="Não" itemValue="false"/>
					<f:selectItem itemLabel="Sim" itemValue="true"/>
				</t:selectOneRadio>
				</td>
			</tr>
			
			
			<tr>
				<th class="required">Máximo de dias que um processo seletivo pode ficar visível:</th>
				<td >
				<h:inputText size="2" value="#{parametrosProgramaPosBean.obj.maxDiasPassadosProcessoSeletivo}" id="maxDiasPassadosProcessoSeletivo" onkeyup="formatarInteiro(this)"/>
				<ufrn:help>
					Caso o valor seja igual a 0 (zero), o processo seletivo permanecerá visível indefinidamente no portal público do programa.
					A contagem dos dias inicia-se a partir da data final do processo seletivo. 
				</ufrn:help>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Teses/Dissertações</td>
			</tr>
			<tr>
				<th class="required">Permitir visualização pública dos arquivos das defesas:</th>
				<td >
				<t:selectOneRadio id="visualizarDefesa" value="#{parametrosProgramaPosBean.obj.visualizarDefesa}" styleClass="noborder">
					<f:selectItem itemLabel="Não" itemValue="false"/>
					<f:selectItem itemLabel="Sim" itemValue="true"/>
				</t:selectOneRadio>
				</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Bancas</td>
			</tr>
			<tr>
				<th class="required">Prazo mínimo para marcar uma banca de qualificação:</th>
				<td >
					<h:inputText size="2" value="#{parametrosProgramaPosBean.obj.prazoMinCadastroBancaQualificacao}" id="prazoMinCadastroBancaQualificacao" onkeyup="formatarInteiro(this)"  maxlength='2'/>
					<ufrn:help>
						Prazo mínimo (em dias) para uma banca de qualificação possa ser marcada após sua solicitação.
					</ufrn:help>	
				</td>
			</tr>
			<tr>
				<th class="required">Prazo mínimo para marcar uma banca de defesa:</th>
				<td >
					<h:inputText size="2" value="#{parametrosProgramaPosBean.obj.prazoMinCadastroBancaDefesa}" id="prazoMinCadastroBancaDefesa" onkeyup="formatarInteiro(this)" maxlength='2'/>
					<ufrn:help>
						Prazo mínimo (em dias) para uma banca de defesa possa ser marcada após sua solicitação.
					</ufrn:help>	
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{parametrosProgramaPosBean.confirmButton}" action="#{parametrosProgramaPosBean.cadastrarValidar}" id="confirmButtonParamProg"/>
						<h:commandButton value="Cancelar" action="#{parametrosProgramaPosBean.cancelar}" onclick="#{confirm}" id="botaoDeCancel"></h:commandButton>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
