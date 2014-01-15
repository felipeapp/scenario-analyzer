<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>Menu da Coordena��o �nica</h2>

<f:view>
	<h:form>
		<table width="100%" border="0" cellspacing="0" cellpadding="2" class="subSistema">
		<tr valign="top">
		    <td width="33%">
				<div class="secao coordenacaoGraduacao">
			        <h3>Coordena��o</h3>
				    <ul>
				    	<li> Matr�cula
				    		<ul>
						    	<li><h:commandLink action="#{ matriculaGraduacao.iniciarMatriculasRegulares}" value="Matricular Discente"/></li>
						        <li><h:commandLink action="#{ alteracaoStatusMatricula.iniciarTrancamentoMatricula}" value="Trancar Matr�cula em Disciplina"/></li>
				    		</ul>
				    	</li>
				    	<li> Consultas/Relat�rios
				    		<ul>
								<li> <a href="${ctx}/graduacao/relatorios/alunos.jsf">Relat�rio de Alunos</a> </li>
				    		</ul>
				    	</li>
				    </ul>
			    </div>
			</td>
			<td width="33%">
			</td>
		    <td width="33%">
		    </td>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>