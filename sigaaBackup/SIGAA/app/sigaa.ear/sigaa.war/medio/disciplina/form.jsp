<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="disciplinaMedioMBean"></a4j:keepAlive>

<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Disciplina</h2>
	<h:form id="form">
	<table class="formulario" width="100%">
		<caption class="formulario">Dados Gerais</caption>
		<tr>
			<th><b>Tipo:</b></th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.tipoComponente.descricao}" />
		</tr>
		<tr>
			<th><b>Nível:</b></th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.descricaoNivelEnsino}" />
		</tr>		
		<tr>
			<th><b>Unidade:</b></th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.unidade.nome}" />
		</tr>			
		<tr>	
			<th class="obrigatorio">Curso:</th>
			<td>
				<h:selectOneMenu value="#{disciplinaMedioMBean.obj.curso.id}" id="curso_atual" >
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{cursoMedio.allUnidadeCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>		
		<tr>
			<th class="obrigatorio">Código: </th>
			<td>
				<h:inputText id="codigo" value="#{disciplinaMedioMBean.obj.codigo}" size="10" maxlength="7" 
				onkeyup="CAPS(this)" disabled="#{disciplinaMedioMBean.obj.id > 0 ? 'true' : 'false' }"/>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Nome:</th>
			<td>
				<h:inputText id="nome"
				value="#{disciplinaMedioMBean.obj.detalhes.nome}" size="90" maxlength="149" onkeyup="CAPS(this)" />
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">
				<h:outputText value="Carga Horária Total:" styleClass="obrigatorio"/>&nbsp;
				<h:outputText value="#{disciplinaMedioMBean.obj.detalhes.chTotal}" id="chtotal"/>h
			</td>
		</tr>
		<tr>
			<th>Carga Horária Teórica:</th>
			<td>
				<h:inputText id="chAula" value="#{disciplinaMedioMBean.obj.detalhes.chAula}" size="4" maxlength="4" onblur="return val();">
					<a4j:support event="onkeyup" oncomplete="return formatarInteiro(this);" reRender="chtotal" actionListener="#{disciplinaMedioMBean.calcularCHTotal}"/>
				</h:inputText>
				<ufrn:help img="/img/ajuda.gif">
					Necessário informar o valor de pelo menos uma Carga Horária.
				</ufrn:help>
			</td>
		</tr>
		<tr>
			<th>Carga Horária Prática:</th>
			<td>
				<h:inputText id="chLab"	value="#{disciplinaMedioMBean.obj.detalhes.chLaboratorio}" size="4" maxlength="4" onblur="return val();">
					<a4j:support event="onkeyup" oncomplete="return formatarInteiro(this);" reRender="chtotal" actionListener="#{disciplinaMedioMBean.calcularCHTotal}"/>		
				</h:inputText>			
			</td> 
		</tr>
		<tr>
			<th>Carga Horária do Docente:</th>
			<td>
				<h:inputText id="chDedicadaDocente"	value="#{disciplinaMedioMBean.obj.detalhes.chDedicadaDocente}" 
					size="4" maxlength="4" onkeyup="return formatarInteiro(this);" onblur="return val();"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">Outras informações</td>
		</tr>			
		<tr>
			<td colspan="2" style="text-align: center;">
			<span style="padding: 5px; font-style: italic; color: #D00">
			 	<b>Atenção!</b> Todas as expressões de equivalências devem ser cercadas por parênteses.
			</span>
			<ufrn:help img="/img/ajuda.gif">
				Exemplo: ( ( DIM0052 ) E ( DIM0301 OU DIM0053 ) )
			</ufrn:help>	
			</td>
		</tr>
		<tr>
			<th>Equivalências:</th>
			<td><h:inputText id="equivalencia"
				value="#{disciplinaMedioMBean.equivalenciaForm}" size="90" maxlength="800"/></td>
		</tr>		
		<tr>
			<td colspan="2">
				<table width="100%">
				<tr>
					<th width="290px">Permite CH Compartilhada entre Docentes:</th>
					<td nowrap="nowrap">
						<h:selectOneRadio value="#{disciplinaMedioMBean.obj.detalhes.permiteChCompartilhada}" id="checkPermiteChCompartilhada" > 
							<f:selectItems value="#{disciplinaMedioMBean.simNao}" />
						</h:selectOneRadio>
					</td>
					<td class="ajuda">
						Marque esta opção caso a soma da carga horária de todos os docentes possa ultrapassar a carga horária da disciplina.
					</td>
				</tr>
				<tr>
					<th width="290px"> Permite Turma com Flexibilidade de Horário:</th>
					<td nowrap="nowrap">
						<h:selectOneRadio id="radioFlexibilidadeHorario" value="#{disciplinaMedioMBean.obj.permiteHorarioFlexivel}">
							<f:selectItems value="#{disciplinaMedioMBean.simNao}" />
						</h:selectOneRadio>
					</td>
					<td class="ajuda">
						Marque esta opção caso as turmas criadas para esta disciplina possam ter horários de aula, que variam durante o ano.
					</td>
				</tr>
				<tr>
					<th width="290px"> Definir Grade de Horários por Docente:</th>
					<td nowrap="nowrap">
						<h:selectOneRadio value="#{disciplinaMedioMBean.obj.permiteHorarioDocenteFlexivel}" id="checkPermiteHorarioDocenteFlexivel" > 
							<f:selectItems value="#{disciplinaMedioMBean.simNao}" />
						</h:selectOneRadio>
					</td>
					<td class="ajuda">
						Marque esta opção caso seja permitido que as turmas desta disciplina possuam docentes associados a ela por períodos específicos, menores que a duração total da turma.
					</td>
				</tr>
				</table>
			</td>
		</tr>
		<tr>
			<th valign="top" id="campoEmenta" class="required">Ementa:</th>
			<td>
				<h:inputTextarea id="ementa"
					value="#{disciplinaMedioMBean.obj.detalhes.ementa}"
					disabled="#{disciplinaMedioMBean.obj.bloco}" cols="85" rows="4" />
			</td>
		</tr>		
		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${disciplinaMedioMBean.obj.id > 0}">
					<h:commandButton id="voltar" value="<< Voltar" action="#{disciplinaMedioMBean.listar}"/>
					</c:if>
					<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{disciplinaMedioMBean.cancelar}" immediate="true" />
					<h:commandButton id="proximo" value="Próximo >>" action="#{disciplinaMedioMBean.submeter}"/>
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>
<script type="text/javascript">
	function val() {
		if ($('form:chAula').value.length <= 0) {
			$('form:chAula').value = 0;
		}	
		if ($('form:chLab').value.length <= 0) {
			$('form:chLab').value = 0;
		}
		if ($('form:chDedicadaDocente').value.length <= 0) {
			$('form:chDedicadaDocente').value = 0;
		}	
		return true;
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>