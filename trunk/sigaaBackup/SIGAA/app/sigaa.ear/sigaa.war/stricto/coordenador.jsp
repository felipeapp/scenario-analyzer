<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>Portal da Coordenação de Pós-Graduação</h2>

<f:view>
	<h:form>
		<table width="100%" border="0" cellspacing="0" cellpadding="2" class="subSistema">
		<tr valign="top">
		    <td width="33%" valign="top">
				<div class="secao coordenacaoGraduacao">
			        <h2>Coordenação</h2>
				    <ul>
				    	<li> Matrículas
				    		<ul>
						    	<li><h:commandLink action="#{ matriculaStricto.iniciar}" value="Matricular Discente"/></li>
				    		</ul>
				    	</li>
				    	<li> Processos Seletivos
				    		<ul>
								<li> <h:commandLink action="#{processoSeletivo.listar}" value="Gerenciar"/></li>
				    		</ul>
				    	</li>
				    	<li> Discentes
				    		<ul>
						    	<li> <h:commandLink action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" id="btaoAtualizarDadoPessoa"/> </li>
						    	<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matrícula" id="btnEmitirAtestadoMat"/> </li>
						    	<li> <h:commandLink action="#{ historicoGraduacao.buscarDiscente }" value="Emitir Histórico" id="btnEmissaoDeHist"/>  </li>
				    		</ul>
				    	</li>
				    </ul>
			    </div>
			</td>
			<td width="40%" class="secao coordenacaoGraduacao" valign="top">
				<h2>Produção Docente</h2>
					<ul>
				    	<li>
				    		<ul>
				    			<li> Lato-Sensu
				    				<ul>
								    	<li><h:commandLink value="Ensino Disciplinas Lato" action="#{ atividadeEnsino.novaLato }" id="linkEnsinoDiscLato"/></li>
								    	<li><h:commandLink value="Orientação Monografia - Especialização" action="#{teseOrientada.novaEspecializacao }" id="linkOrientMonografia"/></li>
							        <li> Residência Médica
							        	<ul>
							        		<li> <h:commandLink value="Ensino Disciplinas Res. Médica" action="#{ atividadeEnsino.novaResidenciaMedica }" id="novResMedica"/> </li>
							        		<li> <h:commandLink value="Orientação de Monografia - Res. Médica" action="#{ teseOrientada.novaResidencia }" id="orientMonogResMedica"/> </li>
							        	</ul>
							        </li>
							    </li>
							    <li> <h:commandLink action="#{teseOrientada.listarStricto}" value="Orientação Mestrado/Doutorado" id="orientacaoDeMestradoDoutorado"/></li>

				    		</ul>
				    	</li>
				   </ul>

			</td>
		    <td width="33%" valign="top">
		    	<center>
		    	<b>${programa.nome}</b>
		    	</center>
		    	<c:if test="${fn:length(acesso.programas) > 1}">
			    	<h:selectOneMenu valueChangeListener="#{unidade.trocarPrograma}" onchange="submit()" style="width: 100%" id="trocaDePrograma">
			    		<f:selectItem itemLabel="--> MUDAR DE PROGRAMA <--" itemValue="0"/>
			    		<f:selectItems value="#{unidade.allProgramasSecretariaCombo}"/>
			    	</h:selectOneMenu>
		    	</c:if>
		    	<br><br>
		    	<h2> Informativos </h2>
		    	Caro Servidor,<br>
				este portal ainda está incompleto. Em breve será o seu canal de gestão
				da pós-graduação. Por enquanto, apenas a produção intelectual está disponível.
		    </td>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>