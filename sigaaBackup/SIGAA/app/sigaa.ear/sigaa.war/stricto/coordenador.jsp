<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>Portal da Coordena��o de P�s-Gradua��o</h2>

<f:view>
	<h:form>
		<table width="100%" border="0" cellspacing="0" cellpadding="2" class="subSistema">
		<tr valign="top">
		    <td width="33%" valign="top">
				<div class="secao coordenacaoGraduacao">
			        <h2>Coordena��o</h2>
				    <ul>
				    	<li> Matr�culas
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
						    	<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" id="btnEmitirAtestadoMat"/> </li>
						    	<li> <h:commandLink action="#{ historicoGraduacao.buscarDiscente }" value="Emitir Hist�rico" id="btnEmissaoDeHist"/>  </li>
				    		</ul>
				    	</li>
				    </ul>
			    </div>
			</td>
			<td width="40%" class="secao coordenacaoGraduacao" valign="top">
				<h2>Produ��o Docente</h2>
					<ul>
				    	<li>
				    		<ul>
				    			<li> Lato-Sensu
				    				<ul>
								    	<li><h:commandLink value="Ensino Disciplinas Lato" action="#{ atividadeEnsino.novaLato }" id="linkEnsinoDiscLato"/></li>
								    	<li><h:commandLink value="Orienta��o Monografia - Especializa��o" action="#{teseOrientada.novaEspecializacao }" id="linkOrientMonografia"/></li>
							        <li> Resid�ncia M�dica
							        	<ul>
							        		<li> <h:commandLink value="Ensino Disciplinas Res. M�dica" action="#{ atividadeEnsino.novaResidenciaMedica }" id="novResMedica"/> </li>
							        		<li> <h:commandLink value="Orienta��o de Monografia - Res. M�dica" action="#{ teseOrientada.novaResidencia }" id="orientMonogResMedica"/> </li>
							        	</ul>
							        </li>
							    </li>
							    <li> <h:commandLink action="#{teseOrientada.listarStricto}" value="Orienta��o Mestrado/Doutorado" id="orientacaoDeMestradoDoutorado"/></li>

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
				este portal ainda est� incompleto. Em breve ser� o seu canal de gest�o
				da p�s-gradua��o. Por enquanto, apenas a produ��o intelectual est� dispon�vel.
		    </td>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>